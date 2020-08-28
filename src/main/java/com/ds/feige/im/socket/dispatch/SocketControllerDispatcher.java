package com.ds.feige.im.socket.dispatch;

import com.ds.base.nodepencies.api.Response;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.socket.annotation.SocketController;
import com.ds.feige.im.socket.annotation.SocketRequestMapping;
import com.ds.feige.im.socket.protocol.SocketRequest;
import com.ds.feige.im.socket.protocol.SocketResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class SocketControllerDispatcher implements BeanPostProcessor {
    static Logger LOGGER= LoggerFactory.getLogger(SocketControllerDispatcher.class);
    private Map<String, SocketMethodHandler> invokeMethods= Maps.newHashMap();
    static final ObjectMapper jsonMapper=new ObjectMapper();
    private List<SocketMethodHandlerInterceptor> interceptors =new ArrayList<>();
    private ValidatorFactory validatorFactory;
    @Autowired
    public SocketControllerDispatcher(ValidatorFactory validatorFactory){
        this.validatorFactory=validatorFactory;
    }
    public void addMethodInterceptor(SocketMethodHandlerInterceptor interceptor){
        this.interceptors.add(interceptor);
    }
    public void doService(WebSocketSession session, SocketRequest socketRequest){
        String path= socketRequest.getPath();
        if(Strings.isNullOrEmpty(path)){
            LOGGER.warn("The path of websocket message is empty:session={}",session);
            return;
        }
        SocketMethodHandler invoker=invokeMethods.get(path);
        if(invoker==null){
            LOGGER.warn("No suitable method mapping was found:path={}",path);
            return;
        }
        this.interceptors.forEach(interceptor -> {
            interceptor.preHandle(session, socketRequest,invoker);
        });
        Throwable dispatchEx=null;
        Object result=null;
        try{
            try{
                result=invoker.invokeForRequest(session, socketRequest);
                for(SocketMethodHandlerInterceptor interceptor:interceptors){
                    interceptor.postHandle(session, socketRequest,result,invoker);
                }
            }catch (Throwable e){
                dispatchEx=e;
                LOGGER.error("Dispatch exception:request={}",socketRequest,dispatchEx);
            }
            processServiceResult(session,socketRequest,result,invoker,dispatchEx);
        }catch (Throwable e){
            for(SocketMethodHandlerInterceptor interceptor:interceptors){
                interceptor.postHandle(session, socketRequest,result,invoker);
            }
        }



    }
    public void processServiceResult(WebSocketSession session,SocketRequest request,Object result,SocketMethodHandler handler,Throwable e) throws Exception{
        if(handler.isNeedResponse()){
            SocketResponse response=new SocketResponse();
            response.setResponseId(request.getRequestId());
            if(e!=null){
                //明确的业务异常
                if(e instanceof WarnMessageException){
                    WarnMessageException we=(WarnMessageException)e;
                    String code=we.getCode();
                    String message=we.getDisplayMessage();
                    response.setCode(code);
                    response.setMessage(message);
                }else{
                    //未知系统异常
                    response.setCode(FeigeWarn.SYSTEM_ERROR.code());
                    response.setMessage(FeigeWarn.SYSTEM_ERROR.displayMessage());
                }
            }else{
                response.setResponseId(request.getRequestId());
                response.setCode(Response.SUCCESS_CODE);
                response.setMessage(Response.SUCCESS_MESSAGE);
                response.setData(result);
            }
            if(session.isOpen()){
                String responseText=jsonMapper.writeValueAsString(response);
                LOGGER.info("Response to client:responseText={}",responseText);
                TextMessage responseMessage=new TextMessage(responseText);
                session.sendMessage(responseMessage);
            }else{
                LOGGER.debug("Need send response to session,but it is closed:session={}",session);
            }
        }
    }
    public void addHandler(String path, SocketMethodHandler handler){
        invokeMethods.put(path,handler);
        LOGGER.info("Add method handler:path={},method={}",path,handler.getMethod());
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        SocketController socketController = AnnotationUtils.getAnnotation(bean.getClass(),SocketController.class);
        SocketRequestMapping classReqMapping=AnnotationUtils.getAnnotation(bean.getClass(),SocketRequestMapping.class);
        if(socketController !=null){
            String path="";
            if(classReqMapping!=null){
                path=classReqMapping.value();
            }
            Method[] declaredMethods=bean.getClass().getDeclaredMethods();
            for(Method method:declaredMethods){
                SocketRequestMapping methodRequestMapping=AnnotationUtils.getAnnotation(method, SocketRequestMapping.class);
                String methodPath=path+methodRequestMapping.value();
                Validator validator=validatorFactory.getValidator();
                SocketMethodHandler invoker=new SocketMethodHandler(methodPath,method,bean,methodRequestMapping.response(),validator);
                addHandler(methodPath,invoker);
            }

        }
        return bean;
    }
}
