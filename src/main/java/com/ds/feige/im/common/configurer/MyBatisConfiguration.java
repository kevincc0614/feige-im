package com.ds.feige.im.common.configurer;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;

@Configuration
@MapperScan("com.ds.feige.im.*.mapper")
public class MyBatisConfiguration {

    @Bean
    IdentifierGenerator identifierGenerator(@Qualifier("longIdKeyGenerator") IdKeyGenerator<Long> longIdKeyGenerator) {
        return entity -> longIdKeyGenerator.generateId();
    }

    @Bean
    MetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    static class MyMetaObjectHandler implements MetaObjectHandler {
        @Override
        public void insertFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)
            this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date()); // 起始版本 3.3.0(推荐)
            // 或者
        }
    }
}
