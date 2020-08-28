package com.ds.feige.im.socket.annotation;


import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketRequestBody {
    /**
     * @return 是否为已登录用户的请求
     * */
    boolean userRequest() default false;
}
