package com.ds.feige.im.common.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ds.base.nodepencies.api.Response;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.constants.FeigeWarn;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleException(HttpServletRequest request, Exception exception) {
        if (request != null) {
            log.warn("HTTP request advice exception:uri={} ", request.getRequestURI(), exception);
        }
        Response response = new Response();
        if (exception instanceof WarnMessageException) {
            WarnMessageException warnMessageException = (WarnMessageException)exception;
            response.setCode(warnMessageException.getCode());
            response.setMessage(warnMessageException.getDisplayMessage());
        } else {
            response.setCode(FeigeWarn.SYSTEM_ERROR.code());
            response.setMessage(FeigeWarn.SYSTEM_ERROR.displayMessage());
        }
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response handleArgumentValidateException(MethodArgumentNotValidException e) {
        Response response = new Response();
        // 打印校验住的所有的错误信息
        StringBuilder sb = new StringBuilder("参数错误：[");
        List<ObjectError> list = e.getBindingResult().getAllErrors();
        for (ObjectError item : list) {
            sb.append(item.getDefaultMessage()).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(']');

        String msg = sb.toString();
        response.setCode(FeigeWarn.REQUEST_VALIDATE_ERROR.code());
        response.setMessage(msg);
        return response;
    }
}
