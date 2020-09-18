package com.ds.feige.im.gateway.socket.annotation;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * @author DC
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SocketController {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
