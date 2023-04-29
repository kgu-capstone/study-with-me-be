package com.kgu.studywithme.study.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BasicAttendance(
        Long studyId,
        int week,
        Long participantId
) {
    @QueryProjection
    public BasicAttendance {}
}
