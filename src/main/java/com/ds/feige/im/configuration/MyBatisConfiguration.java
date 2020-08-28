package com.ds.feige.im.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@MapperScan("com.ds.feige.im.mapper")
public class MyBatisConfiguration  {

    @Bean
    IdentifierGenerator identifierGenerator(@Qualifier("longIdKeyGenerator") IdKeyGenerator<Long> longIdKeyGenerator){
        return entity -> longIdKeyGenerator.generateId();
    }
    @Bean
    MetaObjectHandler metaObjectHandler(){
        return new MyMetaObjectHandler();
    }
    static class MyMetaObjectHandler implements MetaObjectHandler{
        static final Logger LOGGER= LoggerFactory.getLogger(MyMetaObjectHandler.class);
        @Override
        public void insertFill(MetaObject metaObject){
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)
            this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
            this.strictInsertFill(metaObject,"deleted",Boolean.class,false);
        }
        @Override
        public void updateFill(MetaObject metaObject){
            this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date()); // 起始版本 3.3.0(推荐)
            // 或者
        }
    }
}
