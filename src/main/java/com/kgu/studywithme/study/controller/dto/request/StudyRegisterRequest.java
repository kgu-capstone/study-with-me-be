package com.kgu.studywithme.study.controller.dto.request;

import com.kgu.studywithme.study.utils.validator.ValidHashtagCount;
import com.kgu.studywithme.study.utils.validator.ValidStudyType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record StudyRegisterRequest(
        @NotBlank(message = "스터디명은 필수입니다.")
        String name,

        @NotBlank(message = "스터디 설명은 필수입니다.")
        String description,

        @NotNull(message = "참여인원은 필수입니다.")
        Integer capacity,

        @NotNull(message = "카테고리는 필수입니다.")
        Long category,

        @NotBlank(message = "스터디 썸네일은 필수입니다.")
        String thumbnail,

        @ValidStudyType
        @NotBlank(message = "온/오프라인 유무는 필수입니다.")
        String type,

        String province,

        String city,

        @NotNull(message = "스터디 졸업요건은 필수입니다. [최소 출석 횟수]")
        Integer minimumAttendanceForGraduation,

        @ValidHashtagCount
        Set<String> hashtags
) {
}
