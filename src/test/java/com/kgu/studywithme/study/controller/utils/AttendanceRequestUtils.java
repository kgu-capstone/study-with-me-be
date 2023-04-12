package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;

public class AttendanceRequestUtils {
    public static AttendanceRequest createAttendanceRequest() {
        return AttendanceRequest.builder()
                .status(AttendanceStatus.ATTENDANCE.getDescription())
                .week(1)
                .build();
    }
}
