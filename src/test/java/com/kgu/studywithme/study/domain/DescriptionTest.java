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
    @DisplayName("Description을 생성한다")
    void constructSuccess() {
        final String value = "a".repeat(999);

        Description description = Description.from(value);
        assertThat(description.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("Description이 1000자가 넘어갈 경우 생성에 실패한다")
    void constructFailure() {
        final String value = "a".repeat(1001);

        assertThatThrownBy(() -> Description.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DESCRIPTION_LENGTH_OUT_OF_RANGE.getMessage());
    }
}
