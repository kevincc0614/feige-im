package com.ds.feige.im.gateway.socket.dispatch;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ds.base.nodepencies.api.Response;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import com.ds.feige.im.gateway.socket.protocol.SocketPacket;
import com.ds.feige.im.gateway.socket.protocol.SocketResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocketControllerDispatcher implements BeanPostProcessor {
    private Map<String, SocketMethodHandler> invokeMethods = Maps.newHashMap();
    static final ObjectMapper jsonMapper = new ObjectMapper();
    private ValidatorFactory validatorFactory;

    @Autowired
    public SocketControllerDispatcher(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public void doService(WebSocketSession session, SocketPacket socketPacket) {

        String path = socketPacket.getPath();
        if (Strings.isNullOrEmpty(path)) {
            log.warn("The path of websocket message is empty:session={}", session);
            return;
        }
        SocketMethodHandler invoker = invokeMethods.get(path);
        if (invoker == null) {
            log.warn("No suitable method mapping was found:path={}", path);
            return;
        }
        Throwable dispatchEx = null;
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            result = invoker.invokeForRequest(session, socketPacket);
        } catch (Throwable e) {
            dispatchEx = e;
            log.error("Dispatch exception:request={}", socketPacket, dispatchEx);
        } finally {
            processServiceResult(session, socketPacket, result, invoker, dispatchEx);
            long endTime = System.currentTimeMillis();
            log.info("Dispatch socket request duration:path={},duration={}ms", path, (endTime - startTime));
        }

    }

    public void processServiceResult(WebSocketSession session, SocketPacket request, Object result,
        SocketMethodHandler handler, Throwable e) {
        log.info("Ready to process service result to session:result={}", result);
        if (handler.isNeedResponse()) {
            SocketResponse response = new SocketResponse();
            response.setResponseId(request.getRequestId());
            if (e != null) {
                // 明确的业务异常
                if (e instanceof WarnMessageException) {
                    WarnMessageException we = (WarnMessageException)e;
                    String code = we.getCode();
                    String message = we.getDisplayMessage();
                    response.setCode(code);
                    response.setMessage(message);
                } else {
                    // 未知系统异常
                    response.setCode(FeigeWarn.SYSTEM_ERROR.code());
                    response.setMessage(FeigeWarn.SYSTEM_ERROR.displayMessage());
                }
            } else {
                response.setResponseId(request.getRequestId());
                response.setCode(Response.SUCCESS_CODE);
                response.setMessage(Response.SUCCESS_MESSAGE);
                response.setData(result);
            }
            if (session.isOpen()) {
                try {
                    String responseText = jsonMapper.writeValueAsString(response);
                    log.info("Response to client:responseText={}", responseText);
                    TextMessage responseMessage = new TextMessage(responseText);
                    session.sendMessage(responseMessage);
                } catch (IOException ioException) {
                    log.error("Send dispatch result to session error", e);
                }
            } else {
                log.warn("Need send response to session,but it is closed:session={}", session);
            }
        }
    }

    public void addHandler(String path, SocketMethodHandler handler) {
        invokeMethods.put(path, handler);
        log.info("Add method handler:path={},class={},method={}", path,
            handler.getMethod().getDeclaringClass().getSimpleName(), handler.getMethod().getName());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        SocketController socketController = AnnotationUtils.getAnnotation(bean.getClass(), SocketController.class);
        SocketRequestMapping classReqMapping =
            AnnotationUtils.getAnnotation(bean.getClass(), SocketRequestMapping.class);
        if (socketController != null) {
            String path = "";
            if (classReqMapping != null) {
                path = classReqMapping.value();
            }
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                SocketRequestMapping methodRequestMapping =
                    AnnotationUtils.getAnnotation(method, SocketRequestMapping.class);
                if (methodRequestMapping != null) {
                    String methodPath = path + methodRequestMapping.value();
                    Validator validator = validatorFactory.getValidator();
                    SocketMethodHandler invoker =
                        new SocketMethodHandler(methodPath, method, bean, methodRequestMapping.response(), validator);
                    addHandler(methodPath, invoker);
                }
            }

        }
        return bean;
    }
}
