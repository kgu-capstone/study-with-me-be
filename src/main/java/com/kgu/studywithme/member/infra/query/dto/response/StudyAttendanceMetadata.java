package com.kgu.studywithme.member.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record StudyAttendanceMetadata(
        Long studyId,
        int week
) {
    @QueryProjection
    public StudyAttendanceMetadata {}
}
