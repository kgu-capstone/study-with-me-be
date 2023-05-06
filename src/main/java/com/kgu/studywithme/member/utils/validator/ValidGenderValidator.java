package com.kgu.studywithme.member.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidGenderValidator implements ConstraintValidator<ValidGender, String> {
    private static final List<String> ALLOWED_GENDERS = List.of("m", "M", "f", "F");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_GENDERS.contains(value);
    }
}
