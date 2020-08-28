package com.ds.feige.im.account.controller;

import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.constants.SessionAttributeKeys;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户权限验证拦截器
 *
 * @author DC
 */
@Component
public class UserAuthInterceptor extends HandlerInterceptorAdapter {
    UserService userService;

    @Autowired
    public UserAuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NodAuthorizedRequest annotation = handlerMethod.getMethodAnnotation(NodAuthorizedRequest.class);
            if (annotation == null) {
                String authToken = request.getHeader(SessionAttributeKeys.IM_AUTH_TOKEN);
                if (Strings.isNullOrEmpty(authToken)) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return false;
                }
                UserInfo userInfo = userService.verifyToken(authToken);
                return true;
            }
        }
        return super.preHandle(request, response, handler);
    }
}
