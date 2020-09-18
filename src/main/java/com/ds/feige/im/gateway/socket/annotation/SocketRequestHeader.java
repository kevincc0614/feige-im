package com.ds.feige.im.gateway.socket.annotation;

import java.lang.annotation.*;

/**
 * @author DC
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketRequestHeader {
    String value() ;
}
