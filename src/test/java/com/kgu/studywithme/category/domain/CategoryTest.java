package com.kgu.studywithme.category.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.category.domain.Category.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category 도메인 테스트")
class CategoryTest {
    @Test
    @DisplayName("특정 카테고리를 조회한다")
    void test() {
        // given
        final long language = 1L;
        final long interview = 2L;
        final long programming = 3L;
        final long aptituteAndNcs = 4L;
        final long certification = 5L;
        final long etc = 6L;

        // when - then
        assertAll(
                () -> assertThat(Category.from(language)).isEqualTo(LANGUAGE),
                () -> assertThat(Category.from(interview)).isEqualTo(INTERVIEW),
                () -> assertThat(Category.from(programming)).isEqualTo(PROGRAMMING),
                () -> assertThat(Category.from(aptituteAndNcs)).isEqualTo(APTITUDE_NCS),
                () -> assertThat(Category.from(certification)).isEqualTo(CERTIFICATION),
                () -> assertThat(Category.from(etc)).isEqualTo(ETC)
        );
    }
}
