package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StudyThumbnail {
    IMAGE_LANGUAGE_001("language_IELTS.png", "#FFEBEB"),
    IMAGE_LANGUAGE_002("language_JLPT.png", "#FFEBEB"),
    IMAGE_LANGUAGE_003("language_JPT.png", "#FFEBEB"),
    IMAGE_LANGUAGE_004("language_Opic.png", "#FFEBEB"),
    IMAGE_LANGUAGE_005("language_TEPS.png", "#FFFBE8"),
    IMAGE_LANGUAGE_006("language_TOELF.png", "#FFFFFF"),
    IMAGE_LANGUAGE_007("language_TOEIC.png", "#FFFFFF"),
    IMAGE_LANGUAGE_008("language_001.png", "#E7E7E7"),
    IMAGE_LANGUAGE_009("language_toeic.png", "#E7E7E7"),

    IMAGE_INTERVIEW_001("interview_samsung.png", "#E3EDFF"),
    IMAGE_INTERVIEW_002("interview_hyundai.png", "#EFF1FF"),
    IMAGE_INTERVIEW_003("interview_LG.png", "#FFF4F4"),
    IMAGE_INTERVIEW_004("interview_NCS.png", "#FFEBEB"),
    IMAGE_INTERVIEW_005("ncs_ncs.png", "#FFEBEB"),

    IMAGE_PROGRAMMING_001("programming_C.png", "#F0F5FF"),
    IMAGE_PROGRAMMING_002("programming_Java.png", "#FFF2F2"),
    IMAGE_PROGRAMMING_003("programming_JavaScript.png", "#FFFBE5"),
    IMAGE_PROGRAMMING_004("programming_Python.png", "#EAF1FF"),
    IMAGE_PROGRAMMING_005("programming_Baekjoon.png", "#FFFFFF"),
    IMAGE_PROGRAMMING_006("programming_CSS.png", "#E5F9FF"),
    IMAGE_PROGRAMMING_007("programming_HTML.png", "#FFEEDB"),
    IMAGE_PROGRAMMING_008("programming_baekjoon.png", "#E7E7E7"),

    IMAGE_APTITUDE_NCS_001("aptitude_ncs_001.png", "#FF0000"),
    IMAGE_APTITUDE_NCS_002("aptitude_ncs_002.png", "#FF0000"),
    IMAGE_APTITUDE_NCS_003("aptitude_ncs_003.png", "#FF0000"),
    IMAGE_APTITUDE_NCS_004("aptitude_ncs_004.png", "#FF0000"),
    IMAGE_APTITUDE_NCS_005("aptitude_ncs_005.png", "#FF0000"),

    IMAGE_CERTIFICATION_001("certification_CIP.png", "#E7E7E7"),
    IMAGE_CERTIFICATION_002("certification_EIP.png", "#E7E7E7"),
    IMAGE_CERTIFICATION_003("certification_CSSD.png", "#E7E7E7"),
    IMAGE_CERTIFICATION_004("certification_KH.png", "#E7E7E7"),
    IMAGE_CERTIFICATION_005("certification_GTQ.png", "#E7E7E7"),
    IMAGE_CERTIFICATION_006("certification_computerLiteracy.png", "#E7E7E7"),

    IMAGE_ETC_001("etc_teacherExam.png", "#FFFFFF"),
    IMAGE_ETC_002("etc_002.png", "#FF0000"),
    IMAGE_ETC_003("etc_003.png", "#FF0000"),
    IMAGE_ETC_004("etc_004.png", "#FF0000"),
    IMAGE_ETC_005("etc_005.png", "#FF0000"),
    ;

    private final String imageName;
    private final String background;

    public static StudyThumbnail from(String imageName) {
        return Arrays.stream(values())
                .filter(thumbnail -> thumbnail.imageName.equals(imageName))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_THUMBNAIL_NOT_FOUND));
    }
}
