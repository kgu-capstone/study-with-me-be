package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.domain.review.Review;
import lombok.Builder;

import java.time.LocalDateTime;

public record StudyReview(
        StudyMember reviewer, String content, LocalDateTime reviewDate
) {
    @Builder
    public StudyReview {}

    public StudyReview(Review review) {
        this(new StudyMember(review.getWriter()), review.getContent(), review.getModifiedAt());
    }
}
