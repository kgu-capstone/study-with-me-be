package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;

import java.util.Set;

import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;

public class StudyRegisterRequestUtils {
    public static StudyRegisterRequest createOnlineStudyRegisterRequest(Set<String> hashtags) {
        return new StudyRegisterRequest(
                TOEIC.getName(),
                TOEIC.getDescription(),
                TOEIC.getCapacity(),
                TOEIC.getCategory().getId(),
                TOEIC.getThumbnail().getImageName(),
                TOEIC.getType().getBrief(),
                null,
                null,
                TOEIC.getMinimumAttendanceForGraduation(),
                hashtags
        );
    }

    public static StudyRegisterRequest createOfflineStudyRegisterRequest(Set<String> hashtags) {
        return new StudyRegisterRequest(
                TOSS_INTERVIEW.getName(),
                TOSS_INTERVIEW.getDescription(),
                TOSS_INTERVIEW.getCapacity(),
                TOSS_INTERVIEW.getCategory().getId(),
                TOSS_INTERVIEW.getThumbnail().getImageName(),
                TOSS_INTERVIEW.getType().getBrief(),
                TOSS_INTERVIEW.getLocation().getProvince(),
                TOSS_INTERVIEW.getLocation().getCity(),
                TOSS_INTERVIEW.getMinimumAttendanceForGraduation(),
                hashtags
        );
    }
}
