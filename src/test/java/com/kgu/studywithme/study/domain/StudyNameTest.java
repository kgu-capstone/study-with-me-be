package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Study 도메인 {StudyName VO} 테스트")
class StudyNameTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"a", "aaaaaaaaaaaaaaaaaaaa"})
    @DisplayName("StudyName을 생성한다")
    void construct(String value) {
        StudyName name = StudyName.from(value);
        assertThat(name.getValue()).isEqualTo(value);
    }
    
    @Test
    @DisplayName("StudyName이 공백이면 생성에 실패한다")
    void throwExceptionByNameIsBlank() {
        assertThatThrownBy(() -> StudyName.from(""))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.NAME_IS_BLANK.getMessage());
    }
    
    @Test
    @DisplayName("StudyName이 길이 제한을 넘어선다면 생성에 실패한다")
    void throwExceptionByNameLengthOutOfRange() {
        final String value = "a".repeat(21);

        assertThatThrownBy(() -> StudyName.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.NAME_LENGTH_OUT_OF_RANGE.getMessage());
    }
}
