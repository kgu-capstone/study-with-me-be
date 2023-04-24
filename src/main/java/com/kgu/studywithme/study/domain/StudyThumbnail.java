package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StudyThumbnail {
    IMAGE_LANGUAGE_001("language_001.png"),
    IMAGE_LANGUAGE_002("language_002.png"),
    IMAGE_LANGUAGE_003("language_003.png"),
    IMAGE_LANGUAGE_004("language_004.png"),
    IMAGE_LANGUAGE_005("language_005.png"),

    IMAGE_INTERVIEW_001("interview_001.png"),
    IMAGE_INTERVIEW_002("interview_002.png"),
    IMAGE_INTERVIEW_003("interview_003.png"),
    IMAGE_INTERVIEW_004("interview_004.png"),
    IMAGE_INTERVIEW_005("interview_005.png"),

    IMAGE_PROGRAMMING_001("programming_001.png"),
    IMAGE_PROGRAMMING_002("programming_002.png"),
    IMAGE_PROGRAMMING_003("programming_003.png"),
    IMAGE_PROGRAMMING_004("programming_004.png"),
    IMAGE_PROGRAMMING_005("programming_005.png"),

    IMAGE_APTITUDE_NCS_001("aptitude_ncs_001.png"),
    IMAGE_APTITUDE_NCS_002("aptitude_ncs_002.png"),
    IMAGE_APTITUDE_NCS_003("aptitude_ncs_003.png"),
    IMAGE_APTITUDE_NCS_004("aptitude_ncs_004.png"),
    IMAGE_APTITUDE_NCS_005("aptitude_ncs_005.png"),

    IMAGE_CERTIFICATION_001("certification_001.png"),
    IMAGE_CERTIFICATION_002("certification_002.png"),
    IMAGE_CERTIFICATION_003("certification_003.png"),
    IMAGE_CERTIFICATION_004("certification_004.png"),
    IMAGE_CERTIFICATION_005("certification_005.png"),

    IMAGE_ETC_001("etc_001.png"),
    IMAGE_ETC_002("etc_002.png"),
    IMAGE_ETC_003("etc_003.png"),
    IMAGE_ETC_004("etc_004.png"),
    IMAGE_ETC_005("etc_005.png"),
    ;

    private final String imageName;

    public static StudyThumbnail from(String imageName) {
        return Arrays.stream(values())
                .filter(thumbnail -> thumbnail.imageName.equals(imageName))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_THUMBNAIL_NOT_FOUND));
    }
}
