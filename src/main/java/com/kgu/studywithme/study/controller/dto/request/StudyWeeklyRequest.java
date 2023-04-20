package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record StudyWeeklyRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @NotNull(message = "시작날짜는 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startDate,

        @NotNull(message = "종료날짜는 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endDate,

        @NotNull(message = "과제 여부는 필수입니다.")
        Boolean assignmentExists,

        @NotNull(message = "자동 출석 여부는 필수입니다.")
        Boolean autoAttendance,

        List<MultipartFile> files
) {
    @Builder
    public StudyWeeklyRequest {
    }
}
