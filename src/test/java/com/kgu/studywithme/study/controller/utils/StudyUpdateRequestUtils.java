package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;

import java.util.Set;

import static com.kgu.studywithme.fixture.StudyFixture.TOEFL;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;

public class StudyUpdateRequestUtils {
    public static StudyUpdateRequest createOnlineStudyUpdateRequest(Integer capacity, Set<String> hashtags) {
        return new StudyUpdateRequest(
                TOEFL.getName(),
                TOEFL.getDescription(),
                capacity,
                TOEFL.getCategory().getId(),
                TOEFL.getThumbnail().getImageName(),
                TOEFL.getType().getBrief(),
                null,
                null,
                true,
                hashtags
        );
    }

    public static StudyUpdateRequest createOfflineStudyUpdateRequest(Integer capacity, Set<String> hashtags) {
        return new StudyUpdateRequest(
                TOSS_INTERVIEW.getName(),
                TOSS_INTERVIEW.getDescription(),
                capacity,
                TOSS_INTERVIEW.getCategory().getId(),
                TOSS_INTERVIEW.getThumbnail().getImageName(),
                TOSS_INTERVIEW.getType().getBrief(),
                TOSS_INTERVIEW.getLocation().getProvince(),
                TOSS_INTERVIEW.getLocation().getCity(),
                true,
                hashtags
        );
    }
}
