package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.controller.dto.request.StudyCategorySearchRequest;

public record StudyCategoryCondition(
        Category category,
        String sort,
        String type,
        String province,
        String city
) {
    public StudyCategoryCondition(StudyCategorySearchRequest request) {
        this(Category.from(request.category()), request.sort(), request.type(), request.province(), request.city());
    }
}
