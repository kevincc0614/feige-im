package com.ds.feige.im.gateway.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.redisson.api.RBuckets;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.account.dto.LogoutRequest;
import com.ds.feige.im.common.domain.UserIdHolder;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.*;
import com.ds.feige.im.event.service.UserEventService;
import com.ds.feige.im.gateway.DiscoveryService;
import com.ds.feige.im.gateway.domain.SessionUser;
import com.ds.feige.im.gateway.domain.SessionUserFactory;
import com.ds.feige.im.gateway.domain.UserState;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.gateway.socket.connection.UserConnection;
import com.ds.feige.im.gateway.socket.protocol.SocketPacket;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Component
@Slf4j
public class SessionUserServiceImpl implements SessionUserService {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    DiscoveryService discoveryService;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    @Autowired
    SessionUserFactory sessionUserFactory;
    @Autowired
    UserDeviceService userDeviceService;

    @Autowired
    UserEventService userEventService;
    @Override
    public SessionUser getSessionUser(long userId) {
        SessionUser sessionUser = this.sessionUserFactory.getSessionUser(userId);
        return sessionUser;
    }

    @Override
    public Map<Long, UserState> getUserStates(Collection<? extends UserIdHolder> users) {
        Map<String, UserState> bucketResults = getUserStateFromCache(users);
        Map<Long, UserState> stateMap = Maps.newHashMap();
        bucketResults.forEach((k, v) -> {
            stateMap.put(Long.valueOf(k.replace(CacheKeys.SESSION_USER_STATE, "")), v);
        });
        return stateMap;
    }

    Map<String, UserState> getUserStateFromCache(Collection<? extends UserIdHolder> users) {
        RBuckets buckets = this.redissonClient.getBuckets();
        String[] keys = new String[users.size()];
        int i = 0;
        for (UserIdHolder user : users) {
            keys[i] = CacheKeys.SESSION_USER_STATE + user.getUserId();
            i++;
        }
        Map<String, UserState> bucketResults = buckets.get(keys);
        return bucketResults;
    }

    @Override
    public Collection<Long> getOnlineUsers(Collection<? extends UserIdHolder> users) {
        Map<String, UserState> bucketResults = getUserStateFromCache(users);
        Set<Long> onlineUsers = Sets.newHashSet();
        bucketResults.forEach((k, v) -> {
            onlineUsers.add(Long.valueOf(k.replace(CacheKeys.SESSION_USER_STATE, "")));
        });
        return onlineUsers;
    }

    @Override
    public SessionUser login(LoginRequest request, WebSocketSession session) {
        final Long userId = request.getUserId();
        SessionUser sessionUser = this.sessionUserFactory.getSessionUser(userId);
        Map<String, Object> sessionAttributes = session.getAttributes();
        Boolean isLogin = (Boolean)sessionAttributes.get(SessionAttributeKeys.LOGIN);
        // 已经登录过
        if (isLogin != null && isLogin) {
            log.info("This websocketSession has already login:userId={},session={}", userId, session);
            return sessionUser;
        }
        // 构建链接元数据
        ConnectionMeta newConnMeta = new ConnectionMeta();
        newConnMeta.setUserId(userId);
        newConnMeta.setDeviceId(request.getDeviceId());
        newConnMeta.setDeviceType(request.getDeviceType());
        newConnMeta.setInstanceId(discoveryService.getInstanceId());
        newConnMeta.setSessionId(session.getId());
        newConnMeta.setIpAddress(session.getRemoteAddress().toString());
        // 获取分布式锁
        RLock userLock = sessionUser.getLock();
        try {
            if (userLock.tryLock(10L, TimeUnit.SECONDS)) {
                // 同设备号设备在线
                ConnectionMeta sameDeviceIdConnMeta = sessionUser.getConnectionMetaByDeviceId(request.getDeviceId());
                if (sameDeviceIdConnMeta != null) {
                    LogoutRequest logoutRequest = new LogoutRequest();
                    logoutRequest.setUserId(userId);
                    logoutRequest.setDeviceId(sameDeviceIdConnMeta.getDeviceId());
                    logoutRequest.addProperty("deviceId", newConnMeta.getDeviceId());
                    logoutRequest.addProperty("ipAddress", newConnMeta.getIpAddress());
                    logoutRequest.addProperty("type", 1);
                    logout(logoutRequest);
                }
                // 添加新的链接
                sessionUser.connectionEstablished(newConnMeta);
                // 标记为已登录
                sessionAttributes.put(SessionAttributeKeys.LOGIN, true);
                // 记录登录设备
                userDeviceService.deviceLogin(request);
            } else {
                // 超时未获取到锁,返回异常
                log.error("Get session user lock timeout:userId={}", userId);
                throw new WarnMessageException(FeigeWarn.USER_LOCK_TIMEOUT);
            }
        } catch (Exception e) {
            log.error("User login error:userId={}", userId, e);
            throw new WarnMessageException(FeigeWarn.SYSTEM_ERROR);
        } finally {
            userLock.unlock();
        }
        return sessionUser;
    }

    @Override
    public void logout(LogoutRequest logoutRequest) throws Exception {
        // TODO token作废
        // 设备登出
        long userId = logoutRequest.getUserId();
        String deviceId = logoutRequest.getDeviceId();
        SessionUser sessionUser = getSessionUser(userId);
        if (sessionUser == null) {
            log.warn("Session user not exists:userId={}", userId);
            return;
        }
        ConnectionMeta connectionMeta = sessionUser.getConnectionMetaByDeviceId(deviceId);
        if (connectionMeta == null) {
            log.error("Device connection meta not exists:deviceId={}", deviceId);
            return;
        }

        UserConnection connection = sessionUserFactory.getConnection(connectionMeta);
        // 主动登出
        SocketPacket packet = new SocketPacket();
        packet.setPath(SocketPaths.SC_LOGOUT_NOTICE);
        packet.setRequestId(longIdKeyGenerator.generateId());
        packet.setPayload(JsonUtils.toJson(logoutRequest.getProperties()));
        connection.disconnect(packet);
        userDeviceService.deviceLogout(userId, deviceId);
        log.info("User logout success:{}", logoutRequest);
    }

    @Override
    public void disconnect(WebSocketSession session) {
        AtomicBoolean closed = (AtomicBoolean)session.getAttributes().get(SessionAttributeKeys.CLOSED);
        log.info("Ready to close the websocket session:sessionId={},closed={}", session.getId(), closed.get());
        if (closed.compareAndSet(false, true)) {
            Long userId = (Long)session.getAttributes().get("userId");
            if (userId != null) {
                SessionUser sessionUser = new SessionUser(userId, this.redissonClient);
                String deviceId = (String)session.getAttributes().get(SessionAttributeKeys.DEVICE_ID);
                boolean connectionRemoved = sessionUser.disconnectConnection(deviceId);
                if (!connectionRemoved) {
                    log.warn("Delete connection meta cache failure:userId={},deviceId={}", userId, deviceId);
                }

            } else {
                log.warn("WebSocketSession has not login:session={}", session);
            }
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException e) {
                    log.error("Active close the session error:session={}", session, e);
                }
            }
        }
    }

    @Override
    public void sendToUser(Long userId, String path, Object payload, Set<String> excludeConnectionIds) {
        SessionUser user = sessionUserFactory.getSessionUser(userId);
        Set<Map.Entry<String, ConnectionMeta>> connections = user.getConnectionMetas().entrySet();
        log.info("Ready to send message to user:connections={}", connections);
        // 在线推送
        for (Map.Entry<String, ConnectionMeta> entry : connections) {
            try {
                UserConnection connection = sessionUserFactory.getConnection(entry.getValue());
                if (excludeConnectionIds != null && !excludeConnectionIds.isEmpty()) {
                    if (excludeConnectionIds.contains(connection.getId())) {
                        continue;
                    }
                }
                SocketPacket request = new SocketPacket();
                request.setRequestId(longIdKeyGenerator.generateId());
                request.setPayload(JsonUtils.toJson(payload));
                request.setPath(path);
                connection.send(request);
                log.info("Send message to connection:userId={},path={},payload={}", userId, path, payload);
            } catch (IOException e) {
                log.error("Send message to connection error:userId={},path={},payload={}", userId, path, payload);
            }

        }
        // 离线推送

    }

    @Override
    public void sendToUsers(Collection<Long> userIds, String path, Object payload, Set<String> excludeConnectionIds) {
        for (Long userId : userIds) {
            try {
                sendToUser(userId, path, payload, excludeConnectionIds);
            } catch (Exception e) {
                log.error("Send to user error:userId={}", userId, e);
            }

        }
    }

    private void sendEstablishedOK(WebSocketSession session) throws IOException {
        Map<String, Object> attributes = session.getAttributes();
        attributes.put(SessionAttributeKeys.CLOSED, new AtomicBoolean(false));
        SocketPacket request = new SocketPacket();
        request.setPath("/connection/state-change");
        request.setPayload("{\"state\":\"established\"}");
        request.setRequestId(1L);
        TextMessage message = new TextMessage(JsonUtils.toJson(request));
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.sessionUserFactory.addWebSocketSession(session);
        Map<String, Object> attributes = session.getAttributes();
        sendEstablishedOK(session);
        Long userId = (Long)attributes.get(SessionAttributeKeys.USER_ID);
        String sessionId = session.getId();
        String address = session.getRemoteAddress().toString();
        log.info("Connection established:userId={},sessionId={},clientAddress={}", userId, sessionId, address);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId(userId);
        loginRequest.setDeviceId((String)attributes.get(SessionAttributeKeys.DEVICE_ID));
        loginRequest.setDeviceType((DeviceType)attributes.get(SessionAttributeKeys.DEVICE_TYPE));
        loginRequest.setDeviceName((String)attributes.get(SessionAttributeKeys.DEVICE_NAME));
        login(loginRequest, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.debug("WebSocketSession connection closed:sessionId={},closeStatus={}", session.getId(), closeStatus);
        disconnect(session);
        this.sessionUserFactory.removeWebSocketSession(session);
    }

    @Override
    public void pingPong(WebSocketSession session) {
        Long userId = (Long)session.getAttributes().get(SessionAttributeKeys.USER_ID);
        String deviceId = (String)session.getAttributes().get(SessionAttributeKeys.DEVICE_ID);
        SessionUser sessionUser = sessionUserFactory.getSessionUser(userId);
        if (sessionUser == null) {
            disconnect(session);
        } else {
            sessionUser.keepAlive(deviceId);
        }
    }
}
