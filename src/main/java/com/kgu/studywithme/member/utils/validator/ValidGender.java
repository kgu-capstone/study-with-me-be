package com.kgu.studywithme.member.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGenderValidator.class)
public @interface ValidGender {
    String message() default "잘못된 요청입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
