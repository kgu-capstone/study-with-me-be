package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record NoticeRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content
) {
    @Builder
    public NoticeRequest {}
}
