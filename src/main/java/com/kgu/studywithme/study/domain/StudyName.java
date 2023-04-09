package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class StudyName {
    private static final int MAXIMUM_LENGTH = 20;

    @Column(name = "name", nullable = false, unique = true)
    private String value;

    private StudyName(String value) {
        this.value = value;
    }

    public static StudyName from(String value) {
        validateNameIsNotBlank(value);
        validateLengthIsInRange(value);
        return new StudyName(value);
    }

    private static void validateNameIsNotBlank(String value) {
        if (value.isBlank()) {
            throw StudyWithMeException.type(StudyErrorCode.NAME_IS_BLANK);
        }
    }

    private static void validateLengthIsInRange(String value) {
        if (isLengthOutOfRange(value)) {
            throw StudyWithMeException.type(StudyErrorCode.NAME_LENGTH_OUT_OF_RANGE);
        }
    }

    private static boolean isLengthOutOfRange(String name) {
        return MAXIMUM_LENGTH < name.length();
    }
}
