package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Description {
    private static final int MAXIMUM_LENGTH = 1000;

    @Lob
    @Column(name = "description", nullable = false)
    private String value;

    private Description(String value) {
        this.value = value;
    }

    public static Description from(String value) {
        validateLengthIsInRange(value);
        return new Description(value);
    }

    private static void validateLengthIsInRange(String value) {
        if (isLengthOutOfRange(value)) {
            throw StudyWithMeException.type(StudyErrorCode.DESCRIPTION_LENGTH_OUT_OF_RANGE);
        }
    }

    private static boolean isLengthOutOfRange(String name) {
        return MAXIMUM_LENGTH < name.length();
    }
}
