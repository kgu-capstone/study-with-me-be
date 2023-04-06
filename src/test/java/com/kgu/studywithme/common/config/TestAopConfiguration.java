package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.annotation.ValidMemberAspect;
import com.kgu.studywithme.global.annotation.ValidStudyParticipantAspect;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.service.StudyFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class TestAopConfiguration {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @Bean
    public ValidMemberAspect validMemberAspect() {
        return new ValidMemberAspect();
    }

    @Bean
    public ValidStudyParticipantAspect validStudyParticipantAspect() {
        return new ValidStudyParticipantAspect(studyFindService, memberFindService);
    }
}
