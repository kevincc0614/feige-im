package com.ds.feige.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FeigeIMBootstrap {
    static final Logger LOGGER = LoggerFactory.getLogger(FeigeIMBootstrap.class);
    public static void main(String[] args) {
        LOGGER.info("IM Server starting....");
        SpringApplication.run(FeigeIMBootstrap.class, args);
        LOGGER.info("IM Server started");
    }
}
