package com.ds.feige.im.gateway.socket.websocket;

import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.constants.DeviceType;
import com.ds.feige.im.constants.SessionAttributeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;
import java.util.Map;
@Component
public class SimpleHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private UserService userService;
    private static final Logger LOGGER= LoggerFactory.getLogger(SimpleHandshakeInterceptor.class);
    @Autowired
    public SimpleHandshakeInterceptor(UserService userService){
        this.userService=userService;
    }
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //获取登录token
        String token = getParamFromRequest(request, SessionAttributeKeys.IM_AUTH_TOKEN);
        if (token == null) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        //获取设备号,设备类型信息
        String deviceId = getParamFromRequest(request, SessionAttributeKeys.DEVICE_ID);
        String deviceTypeStr = getParamFromRequest(request, SessionAttributeKeys.DEVICE_TYPE);
        try {
            DeviceType deviceType = DeviceType.valueOf(deviceTypeStr);
            attributes.put(SessionAttributeKeys.DEVICE_TYPE, deviceType);
        } catch (Exception e) {
            LOGGER.error("Device type value error:{}", deviceTypeStr);
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        attributes.put(SessionAttributeKeys.DEVICE_ID, deviceId);

        //验证token是否合法
        try {
            UserInfo userInfo = userService.verifyToken(token);
            attributes.put("userId", userInfo.getUserId());
            LOGGER.info("Handshake and verify auth token success:userId={}", userInfo.getUserId());
        } catch (Exception e) {
            LOGGER.error("When handshake verify token error:token={}", token, e);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
    public String getParamFromRequest(ServerHttpRequest request,String key){
        String result=null;
        List<String> header=request.getHeaders().get(key);
        if(header==null||header.isEmpty()){
            if(request instanceof ServletServerHttpRequest ){
                ServletServerHttpRequest servletServerHttpRequest=(ServletServerHttpRequest)request;
                result=servletServerHttpRequest.getServletRequest().getParameter(key);
            }

        }else{
            result=header.get(0);
        }
        return result;
    }
}
