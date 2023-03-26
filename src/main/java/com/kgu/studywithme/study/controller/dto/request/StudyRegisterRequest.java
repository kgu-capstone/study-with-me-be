package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record StudyRegisterRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "설명은 필수입니다.")
        String description,

        @NotNull(message = "카테고리는 필수입니다.")
        long category,

        @NotNull(message = "참여인원은 필수입니다.")
        int capacity,

        @NotBlank(message = "온/오프라인 유무는 필수입니다.")
        String type,

        String province,

        String city,

        Set<String> hashtags
) {
    @Builder
    public StudyRegisterRequest {}
}
