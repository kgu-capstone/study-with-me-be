package com.kgu.studywithme.global.annotation;

import com.kgu.studywithme.global.annotation.validation.ValidHashtagCount;
import com.kgu.studywithme.global.annotation.validation.ValidHashtagCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Annotation ConstraintValidator -> ValidHashtagCountValidator 테스트")
class ValidHashtagCountValidatorTest {
    private ValidHashtagCountValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new ValidHashtagCountValidator();

        ValidHashtagCount validHashtagCount = new ValidHashtagCount() {
            @Override
            public String message() {
                return "";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int min() {
                return 1;
            }

            @Override
            public int max() {
                return 5;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidHashtagCount.class;
            }
        };

        validator.initialize(validHashtagCount);
        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    }
    
    @Test
    @DisplayName("해시태그 개수가 허용하는 최소치[1개]미만이면 validator를 통과하지 못한다")
    void underflowHashtags() {
        // given
        final Set<String> hashtags = Set.of();

        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
        given(builder.addConstraintViolation()).willReturn(context);
        
        // when
        boolean actual = validator.isValid(hashtags, context);

        // then
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("스터디는 최소 1개의 해시태그를 가져야 합니다.");
        verify(builder).addConstraintViolation();
        assertThat(actual).isFalse();
    }
    
    @Test
    @DisplayName("해시태그 개수가 허용하는 최대치[5개]를 초과하면 validator를 통과하지 못한다")
    void overflowHashtags() {
        // given
        final Set<String> hashtags = Set.of("A", "B", "C", "D", "E", "F");

        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
        given(builder.addConstraintViolation()).willReturn(context);
        
        // when
        boolean actual = validator.isValid(hashtags, context);
        
        // then
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("스터디는 최대 5개의 해시태그를 가질 수 있습니다.");
        verify(builder).addConstraintViolation();
        assertThat(actual).isFalse();
    }
    
    @Test
    @DisplayName("해시태그 개수가 1개 ~ 5개 사이면 validator를 통과한다")
    void success() {
        // given
        final Set<String> hashtags = Set.of("A", "B", "C", "D", "E");
        
        // when
        boolean actual = validator.isValid(hashtags, context);
        
        // then
        assertThat(actual).isTrue();
    }
}
