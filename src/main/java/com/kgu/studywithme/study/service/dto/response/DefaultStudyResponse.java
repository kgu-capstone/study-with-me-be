package com.kgu.studywithme.study.service.dto.response;

import lombok.Builder;

import java.util.List;

public record DefaultStudyResponse(
        List<StudyAssembler> result,
        boolean hasNext
) {
    @Builder
    public DefaultStudyResponse {
    }
}
