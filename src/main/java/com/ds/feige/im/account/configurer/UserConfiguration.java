package com.ds.feige.im.account.configurer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author DC
 */
@Configuration
@EnableConfigurationProperties(UserSecurityProperties.class)
public class UserConfiguration {
}
