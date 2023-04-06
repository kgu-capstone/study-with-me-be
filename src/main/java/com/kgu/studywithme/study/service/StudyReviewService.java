package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.review.ReviewRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyReviewService {
    private final StudyRepository studyRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StudyValidator studyValidator;

    @Transactional
    public void write(Long studyId, Long memberId, String content) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
        study.writeReview(member, content);
    }

    @Transactional
    public void remove(Long reviewId, Long memberId) {
        validateReviewWriter(reviewId, memberId);
        reviewRepository.deleteById(reviewId);
    }

    private void validateReviewWriter(Long reviewId, Long memberId) {
        studyValidator.validateReviewWriter(reviewId,memberId);
    }
}