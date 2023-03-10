package com.ds.feige.im.common.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGeneratorFactory;
import com.ds.feige.im.common.util.RuntimeUtils;

@Configuration
public class IdKeyGeneratorConfiguration {
    @Bean("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator(){
        IdKeyGenerator<Long> generator = IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_OUT_DATE_LONG);
        long workerId = RuntimeUtils.getPid() % generator.getMaxWorkId();
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
