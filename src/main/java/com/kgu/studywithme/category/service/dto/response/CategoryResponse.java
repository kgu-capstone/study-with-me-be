package com.kgu.studywithme.category.service.dto.response;

import com.kgu.studywithme.category.domain.Category;
import lombok.Builder;

public record CategoryResponse(
        Long id, String name
) {
    @Builder
    public CategoryResponse {}

    public CategoryResponse(Category category) {
        this(category.getId(), category.getName());
    }
}
