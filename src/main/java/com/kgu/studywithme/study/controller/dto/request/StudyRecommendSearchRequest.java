package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

public record StudyRecommendSearchRequest(
        String sort,
        int page,
        String type
) {
    @Builder
    public StudyRecommendSearchRequest {}

    public boolean isOnline() {
        return "online".equalsIgnoreCase(type);
    }
}
