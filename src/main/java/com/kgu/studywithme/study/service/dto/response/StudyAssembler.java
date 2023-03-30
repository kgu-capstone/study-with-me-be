package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import lombok.Builder;

import java.util.List;

public record StudyAssembler(
        BasicStudy study,
        List<String> hashtags
) {
    @Builder
    public StudyAssembler {}
}
