package com.kgu.studywithme.study.utils.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("Study [Validator] -> ValidStudyTypeValidator 테스트")
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
    void notAllowedStudyType() {
        // given
        final String unknown = "unknown";

        // when
        boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 스터디 타입이 들어오면 validator를 통과한다")
    void allowedStudyType() {
        // given
        final String on = "online";
        final String off = "offline";

        // when
        boolean actual1 = validator.isValid(on, context);
        boolean actual2 = validator.isValid(off, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
