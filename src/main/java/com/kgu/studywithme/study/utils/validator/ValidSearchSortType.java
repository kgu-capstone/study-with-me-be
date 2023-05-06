package com.kgu.studywithme.study.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSearchSortTypeValidator.class)
public @interface ValidSearchSortType {
    String message() default "제공되지 않는 정렬 조건입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
