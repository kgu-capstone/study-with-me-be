package com.kgu.studywithme.study.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record NoticeCommentRequest(
        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
) {
}
