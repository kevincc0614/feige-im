package com.ds.feige.im.push.configure;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;

/**
 * 消息推送配置
 *
 * @author DC
 */
//@Configuration
public class PushConfiguration {
    @Bean
    ApnsClient apnsClient() throws IOException {
        ApnsClient client = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setClientCredentials(new File(""), "").build();
        return client;
    }
}
