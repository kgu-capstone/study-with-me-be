package com.kgu.studywithme.global.annotation;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.service.StudyFindService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidStudyParticipantAspect {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @Before("@annotation(com.kgu.studywithme.global.annotation.ValidStudyParticipant) && args(studyId, memberId)")
    public void checkParticipant(Long studyId, Long memberId) {
        Study study = studyFindService.findByIdWithHost(studyId);
        Member member = memberFindService.findById(memberId);

        study.validateMemberIsParticipant(member);
    }
}
