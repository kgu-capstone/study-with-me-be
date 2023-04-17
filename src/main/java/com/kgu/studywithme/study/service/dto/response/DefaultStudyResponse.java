package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;

import java.util.List;

public record DefaultStudyResponse(
        List<BasicStudy> studyList,
        boolean hasNext
) {
}
