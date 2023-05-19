package com.kgu.studywithme.study.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidStudyTypeValidator implements ConstraintValidator<ValidStudyType, String> {
    private static final List<String> ALLOWED_TYPE = List.of("online", "offline");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_TYPE.contains(value);
    }
}
