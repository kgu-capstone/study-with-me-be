package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content
) {
    @Builder
    public ReviewRequest {}
}
