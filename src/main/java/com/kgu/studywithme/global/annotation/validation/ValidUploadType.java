package com.kgu.studywithme.global.annotation.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUploadTypeValidator.class)
public @interface ValidUploadType {
    String message() default "잘못된 요청입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
