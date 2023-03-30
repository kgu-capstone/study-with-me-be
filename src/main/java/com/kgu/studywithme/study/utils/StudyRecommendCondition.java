package com.kgu.studywithme.study.utils;

import lombok.Builder;

public record StudyRecommendCondition(
        Long memberId,
        String sort,
        boolean isOnline
) {
    @Builder
    public StudyRecommendCondition {
    }
}
