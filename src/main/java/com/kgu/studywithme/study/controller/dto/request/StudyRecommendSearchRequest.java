package com.kgu.studywithme.study.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record StudyRecommendSearchRequest(
        @NotBlank(message = "정렬 조건은 필수입니다.") String sort,
        @NotNull(message = "현재 페이지는 필수입니다.") Integer page,
        String type,
        String province,
        String city
) {
}
