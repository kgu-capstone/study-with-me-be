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
public class CheckStudyParticipantAspect {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @Before("@annotation(CheckStudyParticipant) && args(memberId, studyId, ..)")
    public void checkParticipant(Long studyId, Long memberId) {
        Study study = studyFindService.findById(studyId);
        Member member = memberFindService.findById(memberId);

        study.validateMemberIsParticipant(member);
    }
}
