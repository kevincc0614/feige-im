package com.ds.feige.im.common.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Slf4j
public class HttpRequestBodyAdvisor extends RequestBodyAdviceAdapter {
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        // Parametric object to JSON string
        Method method = parameter.getMethod();
        // Custom log output
        if (log.isInfoEnabled()) {
            log.info("{}#{}: {}", parameter.getContainingClass().getSimpleName(), method.getName(), body);
            // logger.info("json request<=========method:{}#{}", parameter.getContainingClass().getSimpleName(),
            // method.getName());
        }
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
        Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return super.handleEmptyBody(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.getParameterAnnotation(RequestBody.class) != null;
    }
}
