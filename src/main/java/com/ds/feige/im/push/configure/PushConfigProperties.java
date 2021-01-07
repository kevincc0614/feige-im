package com.ds.feige.im.push.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author DC
 */
@Data
@Configuration
@ConfigurationProperties("push")
public class PushConfigProperties {
    private String mode;
    private String googleCredentials;
}
