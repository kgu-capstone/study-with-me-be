package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record StudyCategorySearchRequest(
        @NotNull(message = "카테고리는 필수입니다.") Long category,
        @NotBlank(message = "정렬 조건은 필수입니다.") String sort,
        @NotNull(message = "현재 페이지는 필수입니다.") Integer page,
        String type
) {
    @Builder
    public StudyCategorySearchRequest {}
}
