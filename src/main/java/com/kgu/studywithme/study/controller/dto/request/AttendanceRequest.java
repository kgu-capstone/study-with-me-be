package com.kgu.studywithme.study.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record AttendanceRequest(
        @NotBlank(message = "출석 상태는 필수입니다.")
        String status,

        @NotNull(message = "스터디 주차는 필수입니다.")
        Integer week
) {
    @Builder
    public AttendanceRequest {}
}
