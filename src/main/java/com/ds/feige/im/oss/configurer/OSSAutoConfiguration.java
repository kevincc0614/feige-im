package com.ds.feige.im.oss.configurer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OSS自动配置
 *
 * @author DC
 */
@Configuration
@EnableConfigurationProperties(OSSConfigProperties.class)
public class OSSAutoConfiguration {
}
