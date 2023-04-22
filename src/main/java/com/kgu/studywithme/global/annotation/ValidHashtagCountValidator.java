package com.kgu.studywithme.global.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class ValidHashtagCountValidator implements ConstraintValidator<ValidHashtagCount, Set<String>> {
    private static final String STUDY_HASHTAG_LIST_MIN = "스터디는 최소 %d개의 해시태그를 가져야 합니다.";
    private static final String STUDY_HASHTAG_LIST_MAX = "스터디는 최대 %d개의 해시태그를 가질 수 있습니다.";

    private int min;
    private int max;

    @Override
    public void initialize(ValidHashtagCount constraint) {
        min = constraint.min();
        max = constraint.max();
    }

    @Override
    public boolean isValid(Set<String> hashtags, ConstraintValidatorContext context) {
        if (hashtags.size() < min) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(STUDY_HASHTAG_LIST_MIN, min))
                    .addConstraintViolation();
            return false;
        }

        if (hashtags.size() > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(STUDY_HASHTAG_LIST_MAX, max))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
