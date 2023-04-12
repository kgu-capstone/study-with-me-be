package com.kgu.studywithme.study.domain.attendance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
    ATTENDANCE("출석"),
    LATE("지각"),
    ABSENCE("결석"),
    NON_ATTENDANCE("미출결")
    ;

    private final String description;
}
