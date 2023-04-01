package com.kgu.studywithme.study.service.dto.response;

import lombok.Builder;

import java.util.List;

public record ReviewAssembler(
        int graduateCount, List<StudyReview> reviews
) {
    @Builder
    public ReviewAssembler {}
}
