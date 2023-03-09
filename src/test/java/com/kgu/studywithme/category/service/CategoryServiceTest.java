package com.kgu.studywithme.category.service;

import com.kgu.studywithme.category.service.dto.response.CategoryResponse;
import com.kgu.studywithme.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category [Service Layer] -> CategoryService 테스트")
class CategoryServiceTest extends ServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Test
    @DisplayName("전체 스터디 카테고리를 조회한다")
    void findAllCategory() {
        // when
        List<CategoryResponse> categoryResponse = categoryService.findAll();

        // then
        assertAll(
                () -> assertThat(categoryResponse.size()).isEqualTo(6),
                () -> assertThat(categoryResponse)
                        .extracting("name")
                        .containsExactly(LANGUAGE.getName(), INTERVIEW.getName(), PROGRAMMING.getName(), APTITUDE_NCS.getName(), CERTIFICATION.getName(), ETC.getName())
        );
    }
}