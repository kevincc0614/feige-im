package com.ds.feige.im.gateway.service;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.account.dto.RemoteLoginMsg;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.DeviceType;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.constants.SessionAttributeKeys;
import com.ds.feige.im.gateway.DiscoveryService;
import com.ds.feige.im.gateway.domain.SessionUser;
import com.ds.feige.im.gateway.domain.SessionUserFactory;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.gateway.socket.connection.UserConnection;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DC
 */
@Component
public class SessionUserServiceImpl implements SessionUserService{
    RedissonClient redissonClient;
    DiscoveryService discoveryService;
    RabbitTemplate rabbitTemplate;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    SessionUserFactory sessionUserFactory;
    static final Logger LOGGER= LoggerFactory.getLogger(SessionUserServiceImpl.class);

    @Autowired
    public SessionUserServiceImpl(RedissonClient redissonClient,
                                  DiscoveryService discoveryService,
                                  RabbitTemplate rabbitTemplate,
                                  SessionUserFactory sessionUserFactory) {
        this.redissonClient = redissonClient;
        this.discoveryService = discoveryService;
        this.rabbitTemplate = rabbitTemplate;
        this.sessionUserFactory = sessionUserFactory;
    }

    public void remoteLoginDisconnect(ConnectionMeta oldConnMeta, ConnectionMeta newConnMeta) throws IOException {
        //通知用户其他链接,在其他设备登录
        long userId = oldConnMeta.getUserId();
        LOGGER.info("The user was remote login:userId={},oldConnMeta={},newConnMeta={}", userId, oldConnMeta, newConnMeta);
        SocketRequest sendToClientRequest = new SocketRequest();
        sendToClientRequest.setPath("/user/remote-login");
        sendToClientRequest.setRequestId(longIdKeyGenerator.generateId());
        RemoteLoginMsg msg = new RemoteLoginMsg();
        msg.setDeviceId(newConnMeta.getDeviceId());
        msg.setIpAddress(newConnMeta.getIpAddress());
        sendToClientRequest.setPayload(JsonUtils.toJson(msg));
        UserConnection connection = sessionUserFactory.getConnection(oldConnMeta);
        connection.disconnect(sendToClientRequest);
    }
    @Override
    public SessionUser login(LoginRequest request, WebSocketSession session) {
        final Long userId=request.getUserId();
        SessionUser sessionUser=this.sessionUserFactory.getSessionUser(userId);
        Map<String,Object> sessionAttributes=session.getAttributes();
        Boolean isLogin=(Boolean) sessionAttributes.get(SessionAttributeKeys.LOGIN);
        //已经登录过
        if(isLogin!=null&&isLogin){
            LOGGER.info("This websocketSession has already login:userId={},session={}",userId,session);
            return sessionUser;
        }
        //构建链接元数据
        ConnectionMeta newConnMeta = new ConnectionMeta();
        newConnMeta.setUserId(userId);
        newConnMeta.setDeviceId(request.getDeviceId());
        newConnMeta.setDeviceType(request.getDeviceType());
        newConnMeta.setInstanceId(discoveryService.getInstanceId());
        newConnMeta.setSessionId(session.getId());
        newConnMeta.setIpAddress(session.getRemoteAddress().toString());
        //获取分布式锁
        RLock userLock=sessionUser.getLock();
        try {
            if (userLock.tryLock(10L, TimeUnit.SECONDS)) {
                //同设备号设备在线
                ConnectionMeta sameDeviceIdConnMeta = sessionUser.getMetaByDeviceId(request.getDeviceId());
                if (sameDeviceIdConnMeta != null) {
                    remoteLoginDisconnect(sameDeviceIdConnMeta, newConnMeta);
                }
                //同类型设备在线
                ConnectionMeta sameDeviceTypeConnMeta = sessionUser.getConnectionMetaByType(request.getDeviceType().type);
                //强制断线
                if (sameDeviceTypeConnMeta != null) {
                    remoteLoginDisconnect(sameDeviceTypeConnMeta, newConnMeta);
                }
                //添加新的链接
                sessionUser.addConnectionMeta(newConnMeta);
                //标记为已登录
                sessionAttributes.put(SessionAttributeKeys.LOGIN, true);
            }else{
                // 超时未获取到锁,返回异常
                LOGGER.error("Get session user lock timeout:userId={}",userId);
                throw new WarnMessageException(FeigeWarn.USER_LOCK_TIMEOUT);
            }
        } catch (InterruptedException | IOException e) {
            LOGGER.error("Get the session lock error:userId={}",userId,e);
            throw new WarnMessageException(FeigeWarn.USER_LOCK_INTERRUPT);
        } finally {
            userLock.unlock();
        }
        return sessionUser;
    }

    @Override
    public void logout(WebSocketSession session) {
        //TODO token作废
    }

    @Override
    public void disconnect(WebSocketSession session) {
        AtomicBoolean closed=(AtomicBoolean)session.getAttributes().get(SessionAttributeKeys.CLOSED);
        LOGGER.info("Ready to close the websocket session:sessionId={},closed={}",session.getId(),closed.get());
        if(closed.compareAndSet(false,true)){
            Long userId=(Long) session.getAttributes().get("userId");
            if(userId!=null){
                SessionUser sessionUser= new SessionUser(userId,this.redissonClient);
                String deviceId=(String) session.getAttributes().get(SessionAttributeKeys.DEVICE_ID);
                boolean connectionRemoved=sessionUser.removeConnectionMeta(deviceId);
                if(!connectionRemoved){
                    LOGGER.warn("Delete connection meta cache failure:userId={},deviceId={}",userId,deviceId);
                }

            }else{
                LOGGER.warn("WebSocketSession has not login:session={}",session);
            }
            if(session.isOpen()){
                try {
                    session.close();
                } catch (IOException e) {
                    LOGGER.error("Active close the session error:session={}",session,e);
                }
            }
        }
    }

    @Override
    public void sendToUser(Long userId,String path, Object payload) throws IOException {
        SessionUser user=sessionUserFactory.getSessionUser(userId);
        if(!user.isOnline()){
            //用户不在线
            LOGGER.error("User has no online connection,can not send message:userId={}",userId);
        }else {
            for(Map.Entry<String, ConnectionMeta> entry:user.getConnectionMetas().entrySet()){
                UserConnection connection=sessionUserFactory.getConnection(entry.getValue());
                SocketRequest request=new SocketRequest();
                request.setRequestId(longIdKeyGenerator.generateId());
                request.setPayload(JsonUtils.toJson(payload));
                request.setPath(path);
                connection.send(request);
            }
        }
    }

    @Override
    public void sendToUsers(Collection<Long> userIds, String path, Object payload) {
        for (Long userId : userIds) {
            try {
                sendToUser(userId, path, payload);
            } catch (Exception e) {
                LOGGER.error("Send to user error:userId={}", userId, e);
            }

        }
    }

    private void sendEstablishedOK(WebSocketSession session) throws IOException{
        Map<String, Object> attributes = session.getAttributes();
        attributes.put(SessionAttributeKeys.CLOSED, new AtomicBoolean(false));
        SocketRequest request = new SocketRequest();
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
        Long userId = (Long) attributes.get(SessionAttributeKeys.USER_ID);
        String sessionId = session.getId();
        String address = session.getRemoteAddress().toString();
        LOGGER.info("Connection established:userId={},sessionId={},clientAddress={}", userId, sessionId, address);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId(userId);
        loginRequest.setDeviceId((String) attributes.get(SessionAttributeKeys.DEVICE_ID));
        loginRequest.setDeviceType((DeviceType) attributes.get(SessionAttributeKeys.DEVICE_TYPE));
        login(loginRequest, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        disconnect(session);
        this.sessionUserFactory.removeWebSocketSession(session);
    }

    @Override
    public void pingPong(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(SessionAttributeKeys.USER_ID);
        String deviceId = (String) session.getAttributes().get(SessionAttributeKeys.DEVICE_ID);
        SessionUser sessionUser = sessionUserFactory.getSessionUser(userId);
        if (sessionUser == null) {
            disconnect(session);
        } else {
            sessionUser.keepAlive(deviceId);
        }
    }
}
