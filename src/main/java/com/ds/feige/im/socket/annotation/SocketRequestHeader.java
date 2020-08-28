package com.ds.feige.im.socket.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketRequestHeader {
    String value() ;
}