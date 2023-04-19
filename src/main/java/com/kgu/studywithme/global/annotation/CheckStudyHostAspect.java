package com.kgu.studywithme.global.annotation;

import com.kgu.studywithme.study.service.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckStudyHostAspect {
    private final StudyValidator studyValidator;

    @Before("@annotation(CheckStudyHost) && args(hostId, studyId, ..)")
    public void checkParticipant(Long hostId, Long studyId) {
        studyValidator.validateHost(studyId, hostId);
    }
}
