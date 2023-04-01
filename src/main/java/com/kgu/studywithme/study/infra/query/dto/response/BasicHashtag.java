package com.kgu.studywithme.study.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

public record BasicHashtag(
        Long studyId,
        String name
) {
    @Builder
    @QueryProjection
    public BasicHashtag {}
}
