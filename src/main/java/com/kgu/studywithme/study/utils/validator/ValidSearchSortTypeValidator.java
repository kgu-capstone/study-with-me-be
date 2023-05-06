package com.kgu.studywithme.study.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidSearchSortTypeValidator implements ConstraintValidator<ValidSearchSortType, String> {
    private static final List<String> ALLOWED_SORT_TYPE = List.of("date", "favorite", "review");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_SORT_TYPE.contains(value);
    }
}
