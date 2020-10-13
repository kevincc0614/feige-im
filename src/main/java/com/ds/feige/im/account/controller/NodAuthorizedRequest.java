package com.ds.feige.im.account.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不需要token认证检查注解
 * 在Controller的方法上注解
 *
 * @author DC
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NodAuthorizedRequest {
}
