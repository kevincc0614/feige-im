package com.ds.feige.im.common.configurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ds.feige.im.account.controller.UserAuthInterceptor;

/**
 * web自动配置类
 *
 * @author DC
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    UserAuthInterceptor userAuthInterceptor;

    @Autowired
    public WebConfiguration(UserAuthInterceptor userAuthInterceptor) {
        this.userAuthInterceptor = userAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(this.userAuthInterceptor);
    }
}
