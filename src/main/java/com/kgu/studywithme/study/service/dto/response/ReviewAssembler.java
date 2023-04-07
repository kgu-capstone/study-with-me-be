package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;
import lombok.Builder;

import java.util.List;

public record ReviewAssembler(
        int graduateCount, List<ReviewInformation> reviews
) {
    @Builder
    public ReviewAssembler {}
}
