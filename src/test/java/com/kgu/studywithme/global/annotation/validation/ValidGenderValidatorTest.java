package com.kgu.studywithme.global.annotation.validation;

import com.kgu.studywithme.member.utils.validator.ValidGenderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("Annotation ConstraintValidator -> ValidGenderValidator 테스트")
class ValidGenderValidatorTest {
    private ValidGenderValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidGenderValidator();
        context = mock(ConstraintValidatorContext.class);
    }
    
    @Test
    @DisplayName("허용하지 않는 성별이 들어오면 validator를 통과하지 못한다")
    void notAllowedGender() {
        // given
        final String unknown = "unknown";
        
        // when
        boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }
    
    @Test
    @DisplayName("허용하는 성별이 들어오면 validator를 통과한다")
    void allowedGender() {
        // given
        final String male1 = "m";
        final String male2 = "M";
        final String female1 = "f";
        final String female2 = "F";

        // when
        boolean actual1 = validator.isValid(male1, context);
        boolean actual2 = validator.isValid(male2, context);
        boolean actual3 = validator.isValid(female1, context);
        boolean actual4 = validator.isValid(female2, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue()
        );
    }
}
