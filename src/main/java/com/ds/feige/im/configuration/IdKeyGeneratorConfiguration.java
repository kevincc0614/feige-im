package com.ds.feige.im.configuration;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGeneratorFactory;
import com.ds.feige.im.common.util.RuntimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdKeyGeneratorConfiguration {
    static final Logger LOGGER=LoggerFactory.getLogger(IdKeyGeneratorFactory.class);
    @Bean("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator(){
        IdKeyGenerator<Long> generator=IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_DATE_SHORT_LONG);
        long workerId=RuntimeUtils.getPid()%generator.getMaxWorkId();
        generator.setWorkId((int)workerId);
        return generator;
    }
    @Bean("stringIdKeyGenerator")
    IdKeyGenerator<String> stringIdKeyGenerator(){
        IdKeyGenerator<String> generator=IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_DATE_SHORT_STR);
        long workerId=RuntimeUtils.getPid()%generator.getMaxWorkId();
        generator.setWorkId((int)workerId);
        return generator;
    }
}
