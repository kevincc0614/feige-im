package com.ds.feige.im.account.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.constants.SessionAttributeKeys;
import com.google.common.base.Strings;

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
            String path = request.getServletPath();
            // App调用接口
            if (path.startsWith("/api/")) {
                return super.preHandle(request, response, handler);
            }
            // 普通用户调用接口
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NodAuthorizedRequest annotation = handlerMethod.getMethodAnnotation(NodAuthorizedRequest.class);
            if (annotation == null) {
                String authToken = request.getHeader(SessionAttributeKeys.IM_AUTH_TOKEN);
                if (Strings.isNullOrEmpty(authToken)) {
                    authToken = request.getParameter(SessionAttributeKeys.IM_AUTH_TOKEN);
                    if (Strings.isNullOrEmpty(authToken)) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        return false;
                    }
                }
                UserInfo userInfo = userService.verifyToken(authToken);
                request.setAttribute("userId", userInfo.getUserId());
                return true;
            }
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

}
