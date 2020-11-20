package com.ds.feige.im.common.configurer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author DC
 */
@Configuration
@ConfigurationProperties("feige.rabbitmq.dlx")
@Data
public class RabbitDLXConfigProperties {
    private String exchange;
    private String routingKey;
    private String queue;

}
