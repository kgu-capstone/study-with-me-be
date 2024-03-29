package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.study.controller.dto.request.StudyRecommendSearchRequest;

public record StudyRecommendCondition(
        Long memberId,
        String sort,
        String type,
        String province,
        String city
) {
    public StudyRecommendCondition(Long memberId, StudyRecommendSearchRequest request) {
        this(memberId, request.sort(), request.type(), request.province(), request.city());
    }
}
