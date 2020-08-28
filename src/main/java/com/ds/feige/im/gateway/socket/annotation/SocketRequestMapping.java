package com.ds.feige.im.gateway.socket.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author DC
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketRequestMapping {
    @AliasFor("path")
    String value() default "";
    @AliasFor("value")
    String path() default "";
    /**
     * 是否响应客户端请求
     * */
    boolean response() default true;
}
