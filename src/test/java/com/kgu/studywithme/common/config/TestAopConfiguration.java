package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.aop.CheckMemberIdentityAspect;
import com.kgu.studywithme.global.aop.CheckStudyHostAspect;
import com.kgu.studywithme.global.aop.CheckStudyParticipantAspect;
import com.kgu.studywithme.study.service.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class TestAopConfiguration {
    private final StudyValidator studyValidator;

    @Bean
    public CheckMemberIdentityAspect checkMemberIdentityAspect() {
        return new CheckMemberIdentityAspect();
    }

    @Bean
    public CheckStudyParticipantAspect checkStudyParticipantAspect() {
        return new CheckStudyParticipantAspect(studyValidator);
    }

    @Bean
    public CheckStudyHostAspect checkStudyHostAspect() {
        return new CheckStudyHostAspect(studyValidator);
    }
}
