package com.kgu.studywithme.study.controller.dto.request;

import com.kgu.studywithme.global.annotation.validation.ValidHashtagCount;
import com.kgu.studywithme.global.annotation.validation.ValidStudyType;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record StudyUpdateRequest (
        @NotBlank(message = "스터디명은 필수입니다.")
        String name,

        @NotBlank(message = "스터디 설명은 필수입니다.")
        String description,

        @NotNull(message = "참여인원은 필수입니다.")
        Integer capacity,

        @NotNull(message = "카테고리는 필수입니다.")
        Long category,

        @ValidStudyType
        @NotBlank(message = "온/오프라인 유무는 필수입니다.")
        String type,

        String province,

        String city,

        boolean recruitmentStatus,

        @ValidHashtagCount
        Set<String> hashtags
) {
    @Builder
    public StudyUpdateRequest {}
}
