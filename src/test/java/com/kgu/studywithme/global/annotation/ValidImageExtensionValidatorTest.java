package com.kgu.studywithme.global.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Annotation ConstraintValidator -> ValidImageExtensionValidator 테스트")
class ValidImageExtensionValidatorTest {
    private ValidImageExtensionValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new ValidImageExtensionValidator();
        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    }

    @Test
    @DisplayName("파일이 비어있으면 validator를 통과한다")
    void emptyFile() {
        // given
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        // when
        boolean actual = validator.isValid(file, null);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("허용하지 않는 파일 확장자면 validator를 통과하지 못한다")
    void notAllowedExtension() throws IOException {
        // given
        MultipartFile file = createMockMultipartFile("hello5.webp", "image/webp");

        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
        given(builder.addConstraintViolation()).willReturn(context);

        // when
        boolean actual = validator.isValid(file, context);

        // then
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("이미지는 jpg, jpeg, png, gif만 허용합니다.");
        verify(builder).addConstraintViolation();
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 확장자면 validator를 통과한다")
    void allowedExtension() throws IOException {
        // given
        MultipartFile file = createMockMultipartFile("hello4.png", "image/png");

        // when
        boolean actual = validator.isValid(file, context);

        // then
        assertThat(actual).isTrue();
    }
}
