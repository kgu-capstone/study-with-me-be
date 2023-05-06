package com.kgu.studywithme.study.controller.dto.request;

import com.kgu.studywithme.study.utils.validator.ValidUploadType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public record WeeklyAssignmentSubmitRequest(
        @ValidUploadType
        @NotBlank(message = "과제 제출 타입은 필수입니다.")
        String type,
        MultipartFile file,
        String link
) {
}
