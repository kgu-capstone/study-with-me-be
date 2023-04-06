package com.kgu.studywithme.study.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.review.ReviewRepository;
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
        Study study = studyFindService.findById(studyId);
        Member member = memberFindService.findById(memberId);
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
