package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyValidator {
    private final StudyRepository studyRepository;
    private final NoticeRepository noticeRepository;
    private final CommentRepository commentRepository;

    public void validateName(StudyName name) {
        if (studyRepository.existsByName(name)) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }

    public void validateHost(Long studyId, Long memberId) {
        if (!studyRepository.existsByIdAndParticipantsHostId(studyId, memberId)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST);
        }
    }

    public void validateNoticeWriter(Long noticeId, Long memberId) {
        if (!noticeRepository.existsByIdAndWriterId(noticeId, memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER);
        }
    }

    public void validateCommentWriter(Long commentId, Long memberId) {
        if (!commentRepository.existsByIdAndWriterId(commentId, memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER);
        }
    }

    public void validateCapacity(Long studyId, Integer capacity) {
        Study study = studyRepository.findByIdWithHostAndParticipant(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
        if (study.getParticipants().size() > capacity) {
            throw StudyWithMeException.type(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS);
        }
    }
}
