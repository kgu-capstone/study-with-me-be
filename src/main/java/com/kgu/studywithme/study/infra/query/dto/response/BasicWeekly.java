package com.kgu.studywithme.study.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BasicWeekly(
        Long studyId,
        int week
) {
    @QueryProjection
    public BasicWeekly {}
}
