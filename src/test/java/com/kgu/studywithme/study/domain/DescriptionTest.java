package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Study 도메인 {Description VO} 테스트")
class DescriptionTest {
    @Test
    @DisplayName("Description이 공백이면 생성에 실패한다")
    void throwExceptionByDescriptionIsBlank() {
        assertThatThrownBy(() -> Description.from(""))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DESCRIPTION_IS_BLANK.getMessage());
    }

    @Test
    @DisplayName("Description을 생성한다")
    void construct() {
        final String value = "a".repeat(999);

        Description description = Description.from(value);
        assertThat(description.getValue()).isEqualTo(value);
    }
}
