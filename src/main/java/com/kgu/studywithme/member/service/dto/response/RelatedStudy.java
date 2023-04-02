package com.kgu.studywithme.member.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import lombok.Builder;

import java.util.List;

public record RelatedStudy(
        List<SimpleStudy> participateStudyList,
        List<SimpleStudy> graduatedStudyList,
        List<SimpleStudy> favoriteStudyList
) {
    @Builder
    public RelatedStudy {}
}
