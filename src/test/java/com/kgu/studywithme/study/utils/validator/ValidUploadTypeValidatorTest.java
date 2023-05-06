package com.kgu.studywithme.study.utils.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("Study [Validator] -> ValidUploadTypeValidator 테스트")
class ValidUploadTypeValidatorTest {
    private ValidUploadTypeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidUploadTypeValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("허용하지 않는 업로드 타입이 들어오면 validator를 통과하지 못한다")
    void notAllowedUploadType() {
        // given
        final String unknown = "unknown";

        // when
        boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 업로드 타입이 들어오면 validator를 통과한다")
    void allowedUploadType() {
        // given
        final String link = "link";
        final String file = "file";

        // when
        boolean actual1 = validator.isValid(link, context);
        boolean actual2 = validator.isValid(file, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
