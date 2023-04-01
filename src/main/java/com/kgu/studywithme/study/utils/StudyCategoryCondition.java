package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.controller.dto.request.StudyCategorySearchRequest;
import lombok.Builder;

public record StudyCategoryCondition(
        Category category,
        String sort,
        String type,
        String province,
        String city
) {
    @Builder
    public StudyCategoryCondition {}

    public StudyCategoryCondition(StudyCategorySearchRequest request) {
        this(Category.from(request.category()), request.sort(), request.type(), request.province(), request.city());
    }
}
