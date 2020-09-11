package com.ds.feige.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@SpringBootApplication
@EnableTransactionManagement
public class FeigeIMBootstrap {
    static final Logger LOGGER = LoggerFactory.getLogger(FeigeIMBootstrap.class);
    public static void main(String[] args) {
        LOGGER.info("FeigeIM Server starting....");
        ConfigurableApplicationContext context=SpringApplication.run(FeigeIMBootstrap.class,args);
        DataSource dataSource=context.getBean(DataSource.class);
        LOGGER.info("FeigeIM Server started");
    }
}
