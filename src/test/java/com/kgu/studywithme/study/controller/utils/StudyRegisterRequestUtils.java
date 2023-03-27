package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;

import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;

public class StudyRegisterRequestUtils {
    public static StudyRegisterRequest createOnlineStudyRegisterRequest() {
        return StudyRegisterRequest.builder()
                .name(TOEIC.getName())
                .description(TOEIC.getDescription())
                .category(TOEIC.getCategory().getId())
                .capacity(TOEIC.getCapacity())
                .type(TOEIC.getType().getDescription())
                .hashtags(TOEIC.getHashtags())
                .build();
    }

    public static StudyRegisterRequest createOfflineStudyRegisterRequest() {
        return StudyRegisterRequest.builder()
                .name(TOSS_INTERVIEW.getName())
                .description(TOSS_INTERVIEW.getDescription())
                .category(TOSS_INTERVIEW.getCategory().getId())
                .capacity(TOSS_INTERVIEW.getCapacity())
                .type(TOSS_INTERVIEW.getType().getDescription())
                .province(TOSS_INTERVIEW.getArea().getProvince())
                .city(TOSS_INTERVIEW.getArea().getCity())
                .hashtags(TOSS_INTERVIEW.getHashtags())
                .build();
    }
}
