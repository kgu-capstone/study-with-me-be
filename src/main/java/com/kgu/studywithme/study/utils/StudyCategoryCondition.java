package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.category.domain.Category;
import lombok.Builder;

public record StudyCategoryCondition(
        Category category,
        String sort,
        boolean isOnline
) {
    @Builder
    public StudyCategoryCondition {}
}
