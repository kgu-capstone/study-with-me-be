package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

public record StudySearchRequest(
        Long category,
        String sort,
        int page,
        boolean isOnline
) {
    @Builder
    public StudySearchRequest {}
}
