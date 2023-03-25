package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.exception.CategoryErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StudyType {
    ONLINE("온라인"),
    OFFLINE("오프라인")
    ;

    private final String description;

    public static StudyType from(String description) {
        return Arrays.stream(values())
                .filter(study -> study.description.equals(description))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(CategoryErrorCode.CATEGORY_NOT_EXIST));
    }
}
