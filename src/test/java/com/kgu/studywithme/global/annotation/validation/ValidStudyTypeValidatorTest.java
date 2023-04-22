package com.kgu.studywithme.global.annotation.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("Annotation ConstraintValidator -> ValidStudyTypeValidator 테스트")
class ValidStudyTypeValidatorTest {
    private ValidStudyTypeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidStudyTypeValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("허용하지 않는 스터디 타입이 들어오면 validator를 통과하지 못한다")
    void notAllowedGender() {
        // given
        final String unknown = "unknown";

        // when
        boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 스터디 타입이 들어오면 validator를 통과한다")
    void allowedGender() {
        // given
        final String on1 = "on";
        final String on2 = "ON";
        final String off1 = "off";
        final String off2 = "OFF";

        // when
        boolean actual1 = validator.isValid(on1, context);
        boolean actual2 = validator.isValid(on2, context);
        boolean actual3 = validator.isValid(off1, context);
        boolean actual4 = validator.isValid(off2, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue()
        );
    }
}
