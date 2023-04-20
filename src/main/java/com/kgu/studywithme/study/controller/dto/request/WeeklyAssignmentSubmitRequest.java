package com.kgu.studywithme.study.controller.dto.request;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public record WeeklyAssignmentSubmitRequest(
        @NotBlank(message = "과제 제출 타입은 필수입니다.")
        String type,
        MultipartFile file,
        String link
) {
}
