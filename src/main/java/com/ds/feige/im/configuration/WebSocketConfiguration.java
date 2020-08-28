package com.ds.feige.im.configuration;

import com.ds.feige.im.service.user.SessionUserService;
import com.ds.feige.im.socket.annotation.SocketController;
import com.ds.feige.im.socket.websocket.SimpleHandshakeInterceptor;
import com.ds.feige.im.socket.websocket.WebSocketDispatcherHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * @author caedmon
 */
@Configuration
@EnableWebSocket
@ComponentScan(includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = SocketController.class)})
public class WebSocketConfiguration implements WebSocketConfigurer {
    WebSocketDispatcherHandler webSocketDispatcherHandler;
    SimpleHandshakeInterceptor simpleHandshakeInterceptor;

    @Autowired
    public WebSocketConfiguration(WebSocketDispatcherHandler webSocketDispatcherHandler, SimpleHandshakeInterceptor simpleHandshakeInterceptor, SessionUserService sessionUserService) {
        this.webSocketDispatcherHandler = webSocketDispatcherHandler;
        this.simpleHandshakeInterceptor = simpleHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketDispatcherHandler, "/ws-endpoint")
                .addInterceptors(simpleHandshakeInterceptor)
                .setAllowedOrigins("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean containerFactoryBean = new ServletServerContainerFactoryBean();
        containerFactoryBean.setMaxBinaryMessageBufferSize(8192);
        containerFactoryBean.setMaxTextMessageBufferSize(8192);
        return containerFactoryBean;
    }
}
