package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.annotation.ValidMemberAspect;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestAopConfiguration {
    @Bean
    public ValidMemberAspect validMemberAspect() {
        return new ValidMemberAspect();
    }
}
