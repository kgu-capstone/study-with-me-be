package com.kgu.studywithme.member.infra.query.dto.response;

import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.querydsl.core.annotations.QueryProjection;

public record AttendanceRatio(
        AttendanceStatus status, int count
) {
    @QueryProjection
    public AttendanceRatio {}
}
