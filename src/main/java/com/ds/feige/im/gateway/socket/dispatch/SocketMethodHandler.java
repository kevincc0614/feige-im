package com.ds.feige.im.gateway.socket.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.WebSocketSession;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserRequest;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.constants.SessionAttributeKeys;
import com.ds.feige.im.gateway.socket.annotation.UserId;
import com.ds.feige.im.gateway.socket.protocol.SocketPacket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

import cn.hutool.core.convert.impl.NumberConverter;

public class SocketMethodHandler {
    private String path;
    private Method method;
    private Object bean;
    private boolean needResponse;
    private Validator validator;
    private static final Logger LOGGER= LoggerFactory.getLogger(SocketMethodHandler.class);
    public SocketMethodHandler(String path, Method method, Object bean, boolean needResponse, Validator validator){
        this.path=path;
        this.method=method;
        this.bean=bean;
        this.needResponse=needResponse;
        this.validator=validator;
    }
    public Object getBean(){
        return this.bean;
    }
    public Method getMethod(){
        return this.method;
    }

    public Object invokeForRequest(WebSocketSession session, SocketPacket socketPacket) throws Exception {
        Object[] args = getMethodArgumentValues(session, socketPacket);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Invoke method arguments: " + Arrays.toString(args));
        }
        return doInvoke(args);
    }
    public Long getUserIdFromSession(WebSocketSession session){
        Map<String,Object> sessionAttrs=session.getAttributes();
        //???????????????
        if(!sessionAttrs.containsKey(SessionAttributeKeys.USER_ID)){
            return null;
        }
        Long userId=(Long) sessionAttrs.get(SessionAttributeKeys.USER_ID);
        return userId;
    }

    public Object[] getMethodArgumentValues(WebSocketSession session, SocketPacket socketPacket)
        throws JsonProcessingException, IllegalAccessException, InstantiationException {
        Parameter[] parameters=method.getParameters();
        Object[] parameterValues=new Object[parameters.length];
        int bodyParamCount=0;
        String payload = socketPacket.getPayload();
        Map<String, String> headers = socketPacket.getHeaders();
        Map<String, Object> attributes = session.getAttributes();
        //payload??????map???????????????,???json??????????????????
        Map<String,Object> payloadMap=null;
        for(int i=0;i<parameters.length;i++){
            Parameter parameter=parameters[i];
            if (parameter.getType().isAssignableFrom(SocketPacket.class)) {
                parameterValues[i] = socketPacket;
                continue;
            }
            UserId userIdAnno = AnnotationUtils.getAnnotation(parameter, UserId.class);
            if (userIdAnno != null) {
                if (parameter.getType() == Long.class || parameter.getType() == long.class) {
                    parameterValues[i] = getUserIdFromSession(session);
                    continue;
                } else {
                    throw new IllegalStateException("@UserId annotation only used for Long/long type parameter");
                }
            }
            RequestBody requestBody=AnnotationUtils.getAnnotation(parameter, RequestBody.class);
            if(requestBody!=null){
                if(bodyParamCount>1){
                    throw new IllegalStateException("@SocketRequestBody ????????????????????????????????????");
                }
                parameterValues[i]= JsonUtils.jsonToBean(payload,parameter.getType());
                if(UserRequest.class.isAssignableFrom(parameter.getType())){
                    Long userId=getUserIdFromSession(session);
                    if (parameterValues[i] == null) {
                        parameterValues[i] = parameter.getType().newInstance();
                    }
                    ((UserRequest)parameterValues[i]).setUserId(userId);
                }
                bodyParamCount++;
                List<String> validateResult=validateParams(parameter,parameterValues[i]);
                if(!validateResult.isEmpty()){
                    throw new WarnMessageException(FeigeWarn.REQUEST_VALIDATE_ERROR,validateResult.toString());
                }
                continue;
            }
            RequestHeader headerAnnotation=AnnotationUtils.getAnnotation(parameter, RequestHeader.class);
            if(headerAnnotation!=null){
                if(parameter.getType().isAssignableFrom(String.class)){
                    String key=headerAnnotation.value();
                    String header=null;
                    if(headers!=null){
                        header = socketPacket.getHeaders().get(key);
                    }
                    parameterValues[i]=header;
                    continue;
                }else{
                    throw new IllegalStateException("@RequestHeader ??????????????????????????????String");
                }
            }
            RequestAttribute attributeAnnotation = AnnotationUtils.getAnnotation(parameter, RequestAttribute.class);
            if (attributeAnnotation != null) {
                String key = attributeAnnotation.value();
                Object attribute = null;
                if (attributes != null) {
                    attribute = attributes.get(key);
                }
                parameterValues[i] = attribute;
                continue;
            }
            //TODO ????????????????????? Conventions.getVariableNameForParameter(parameter);
//            SocketSession sessionAnnotation=AnnotationUtils.getAnnotation(parameter, SocketSession.class);
//            if(sessionAnnotation!=null){
            //WebSocketSession
                if(parameter.getType().isAssignableFrom(WebSocketSession.class)){
                    parameterValues[i]=session;
                    continue;
                }
            RequestParam paramAnnotation=AnnotationUtils.getAnnotation(parameter, RequestParam.class);
            if(paramAnnotation!=null){
                String paramName=paramAnnotation.value();
                //???????????????????????????
                if(payloadMap==null){
                    payloadMap=JsonUtils.jsonToBean(payload, HashMap.class);
                }
                //TODO required ????????????????????????
                Object mapValue = payloadMap.getOrDefault(paramName, paramAnnotation.defaultValue());
                if (Number.class.isAssignableFrom(parameter.getType())) {
                    if (parameter.getType() == Long.class || parameter.getType() == long.class) {
                        parameterValues[i] =
                            new NumberConverter(((Number)mapValue).getClass()).convert(mapValue, 0).longValue();
                    }
                } else {
                    parameterValues[i] = mapValue;
                }

                continue;
            }
        }
        return parameterValues;

    }
    protected Object doInvoke( Object... args) throws Exception {
        ReflectionUtils.makeAccessible(getMethod());
//        getMethod().setAccessible(true);
        try {
            return getMethod().invoke(getBean(), args);
        }
        catch (IllegalArgumentException ex) {
            assertTargetBean(getMethod(), getBean(), args);
            String text = (ex.getMessage() != null ? ex.getMessage() : "Illegal argument");
            throw new IllegalStateException(formatInvokeError(text, args), ex);
        }
        catch (InvocationTargetException ex) {
            // Unwrap for HandlerExceptionResolvers ...
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            }
            else if (targetException instanceof Error) {
                throw (Error) targetException;
            }
            else if (targetException instanceof Exception) {
                throw (Exception) targetException;
            }
            else {
                throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
            }
        }
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public List<String> validateParams(Parameter parameter,Object target){
        Valid valid=AnnotationUtils.getAnnotation(parameter,Valid.class);
        List<String> validateMessages= Lists.newArrayList();
        if(valid!=null){
            Set<ConstraintViolation<Object>> constraintViolations= validator.validate(target);
            for(ConstraintViolation violation:constraintViolations){
                validateMessages.add(violation.getPropertyPath()+":"+violation.getMessage());
            }
        }
        return validateMessages;
    }
    protected static String formatArgumentError(MethodParameter param, String message) {
        return "Could not resolve parameter [" + param.getParameterIndex() + "] in " +
                param.getExecutable().toGenericString() + (StringUtils.hasText(message) ? ": " + message : "");
    }

    protected void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() +
                    "' is not an instance of the actual controller bean class '" +
                    targetBeanClass.getName() + "'. If the controller requires proxying " +
                    "(e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(formatInvokeError(text, args));
        }
    }

    protected String formatInvokeError(String text, Object[] args) {
        String formattedArgs = IntStream.range(0, args.length)
                .mapToObj(i -> (args[i] != null ?
                        "[" + i + "] [type=" + args[i].getClass().getName() + "] [value=" + args[i] + "]" :
                        "[" + i + "] [null]"))
                .collect(Collectors.joining(",\n", " ", " "));
        return text + "\n" +
                "Controller [" + getBean().getClass().getName() + "]\n" +
                "Method [" + getMethod().toGenericString() + "] " +
                "with argument values:\n" + formattedArgs;
    }
}
