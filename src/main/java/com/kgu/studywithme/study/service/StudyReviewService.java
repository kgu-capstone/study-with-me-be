package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.review.Review;
import com.kgu.studywithme.study.domain.review.ReviewRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyReviewService {
    private final ReviewRepository reviewRepository;
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public void write(Long studyId, Long memberId, String content) {
        Study study = studyFindService.findByIdWithParticipants(studyId);
        Member member = memberFindService.findById(memberId);

        study.writeReview(member, content);
    }

    @Transactional
    public void remove(Long reviewId, Long memberId) {
        validateReviewWriter(reviewId, memberId);
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    public void update(Long reviewId, Long memberId, String content) {
        validateReviewWriter(reviewId, memberId);

        Review review = findById(reviewId);
        review.updateReview(content);
    }

    private void validateReviewWriter(Long reviewId, Long memberId) {
        studyValidator.validateReviewWriter(reviewId, memberId);
    }

    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.REVIEW_NOT_FOUND));
    }
}
