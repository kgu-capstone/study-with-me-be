package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.annotation.CheckMemberIdentityAspect;
import com.kgu.studywithme.global.annotation.CheckStudyHostAspect;
import com.kgu.studywithme.global.annotation.CheckStudyParticipantAspect;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.service.StudyFindService;
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
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @Bean
    public CheckMemberIdentityAspect checkMemberIdentityAspect() {
        return new CheckMemberIdentityAspect();
    }

    @Bean
    public CheckStudyParticipantAspect checkStudyParticipantAspect() {
        return new CheckStudyParticipantAspect(studyFindService, memberFindService);
    }

    @Bean
    public CheckStudyHostAspect checkStudyHostAspect() {
        return new CheckStudyHostAspect(studyValidator);
    }
}
