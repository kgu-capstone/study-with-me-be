package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyValidator {
    private final StudyRepository studyRepository;

    public void validateName(StudyName name) {
        if (studyRepository.existsByName(name)) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }

    public void validateHost(Study study, Long memberId) {
        if (!study.getHost().getId().equals(memberId)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST);
        }
    }

    public void validateNoticeWriter(Notice notice, Long memberId) {
        if (!notice.getWriter().getId().equals(memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER);
        }
    }
}