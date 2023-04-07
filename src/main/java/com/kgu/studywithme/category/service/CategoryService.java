package com.kgu.studywithme.category.service;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.category.service.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    public List<CategoryResponse> findAll() {
        return Arrays.stream(Category.values())
                .map(CategoryResponse::new)
                .toList();
    }
}
