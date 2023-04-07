package com.kgu.studywithme.category.service.dto.response;

import com.kgu.studywithme.category.domain.Category;

public record CategoryResponse(
        Long id, String name
) {
    public CategoryResponse(Category category) {
        this(category.getId(), category.getName());
    }
}
