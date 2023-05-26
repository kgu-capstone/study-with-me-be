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
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String value;

    private Description(String value) {
        this.value = value;
    }

    public static Description from(String value) {
        validateDescriptionIsNotBlank(value);
        return new Description(value);
    }

    private static void validateDescriptionIsNotBlank(String value) {
        if (value.isBlank()) {
            throw StudyWithMeException.type(StudyErrorCode.DESCRIPTION_IS_BLANK);
        }
    }
}
