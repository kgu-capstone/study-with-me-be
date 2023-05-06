package com.kgu.studywithme.study.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidUploadTypeValidator implements ConstraintValidator<ValidUploadType, String> {
    private static final List<String> ALLOWED_TYPE = List.of("link", "file");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_TYPE.contains(value);
    }
}
