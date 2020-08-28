package com.ds.feige.im.pojo.domain;

import com.ds.feige.im.discovery.DiscoveryService;
import com.ds.feige.im.socket.connection.ConnectionMeta;
import com.ds.feige.im.socket.connection.UserConnection;
import com.google.common.collect.Maps;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * SessionUser工厂类
 *
 * @author DC
 */
@Component
public class SessionUserFactory {
    RedissonClient redissonClient;
    DiscoveryService discoveryService;
    RabbitTemplate rabbitTemplate;
    Map<String, WebSocketSession> webSocketSessionCache = Maps.newConcurrentMap();
    @Autowired
    public SessionUserFactory(RedissonClient redissonClient, DiscoveryService discoveryService, RabbitTemplate rabbitTemplate){
        this.redissonClient=redissonClient;
        this.discoveryService=discoveryService;
        this.rabbitTemplate=rabbitTemplate;

    }
    public SessionUser getSessionUser(long userId){
        return new SessionUser(userId,redissonClient);
    }
    public void addWebSocketSession(WebSocketSession session){
        this.webSocketSessionCache.put(session.getId(),session);
    }
    public void removeWebSocketSession(String sessionId){
        this.webSocketSessionCache.remove(sessionId);
    }
    public UserConnection getConnection(ConnectionMeta meta){
        UserConnection connection=null;
        //本机服务器链接
        if(meta.getInstanceId().equals(discoveryService.getInstanceId())){
            WebSocketSession session= webSocketSessionCache.get(meta.getSessionId());
            connection=new LUserConnection(meta,session);
        }else{
            //远程服务器链接
            connection=new RUserConnection(meta,rabbitTemplate);
        }
        return connection;
    }
}
