package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;

import static com.kgu.studywithme.fixture.StudyFixture.TOEFL;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;

public class StudyUpdateRequestUtils {
    public static StudyUpdateRequest createOnlineStudyUpdateRequest(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(TOEFL.name())
                .description(TOEFL.getDescription())
                .capacity(capacity)
                .category(TOEFL.getCategory().getId())
                .thumbnail(TOEFL.getThumbnail().getImageName())
                .type(TOEFL.getType().getBrief())
                .recruitmentStatus(true)
                .hashtags(TOEFL.getHashtags())
                .build();
    }

    public static StudyUpdateRequest createOfflineStudyUpdateRequest(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(TOSS_INTERVIEW.getName())
                .description(TOSS_INTERVIEW.getDescription())
                .capacity(capacity)
                .category(TOSS_INTERVIEW.getCategory().getId())
                .thumbnail(TOSS_INTERVIEW.getThumbnail().getImageName())
                .type(TOSS_INTERVIEW.getType().getBrief())
                .province(TOSS_INTERVIEW.getLocation().getProvince())
                .city(TOSS_INTERVIEW.getLocation().getCity())
                .recruitmentStatus(true)
                .hashtags(TOSS_INTERVIEW.getHashtags())
                .build();
    }
}
