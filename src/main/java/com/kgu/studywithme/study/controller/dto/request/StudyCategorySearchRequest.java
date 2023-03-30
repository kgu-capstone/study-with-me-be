package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

public record StudyCategorySearchRequest(
        Long category,
        String sort,
        int page,
        String type
) {
    @Builder
    public StudyCategorySearchRequest {}

    public boolean isOnline() {
        return "online".equalsIgnoreCase(type);
    }
}
