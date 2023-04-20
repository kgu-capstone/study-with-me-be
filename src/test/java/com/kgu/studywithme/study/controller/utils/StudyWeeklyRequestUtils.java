package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.fixture.WeekFixture;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class StudyWeeklyRequestUtils {
    public static StudyWeeklyRequest createWeekWithAssignmentRequest(WeekFixture fixture, List<MultipartFile> files, boolean autoAttendance) {
        return StudyWeeklyRequest.builder()
                .title(fixture.getTitle())
                .content("지정된 기간까지 과제 제출해주세요.")
                .startDate(fixture.getPeriod().getStartDate())
                .endDate(fixture.getPeriod().getEndDate())
                .assignmentExists(true)
                .autoAttendance(autoAttendance)
                .files(files)
                .build();
    }

    public static StudyWeeklyRequest createWeekRequest(WeekFixture fixture, List<MultipartFile> files) {
        return StudyWeeklyRequest.builder()
                .title(fixture.getTitle())
                .content("지정된 시간까지 다들 줌에 접속해주세요.")
                .startDate(fixture.getPeriod().getStartDate())
                .endDate(fixture.getPeriod().getEndDate())
                .assignmentExists(false)
                .autoAttendance(false)
                .files(files)
                .build();
    }
}
