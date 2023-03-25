package com.kgu.studywithme.study.controller.dto.request;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyType;
import com.kgu.studywithme.study.domain.participant.Capacity;
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

    public Study toEntity() {
        if (type.equals("온라인")) {
            return Study.builder()
                    .name(StudyName.from(name))
                    .description(Description.from(description))
                    .category(Category.from(category))
                    .capacity(Capacity.from(capacity))
                    .type(StudyType.from(type))
                    .hashtags(hashtags)
                    .build();
        } else if (type.equals("오프라인")) {
            return Study.builder()
                    .name(StudyName.from(name))
                    .description(Description.from(description))
                    .category(Category.from(category))
                    .capacity(Capacity.from(capacity))
                    .area(null)
                    .type(StudyType.from(type))
                    .hashtags(hashtags)
                    .build();
        } else {
            throw StudyWithMeException.type(GlobalErrorCode.VALIDATION_ERROR);
        }
    }
}
