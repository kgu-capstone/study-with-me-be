package com.kgu.studywithme.study.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

@Getter
public class StudyWeeksDTO {
    private Long studyId;
    private List<Integer> weeks;

    @QueryProjection
    public StudyWeeksDTO(Long studyId, List<Integer> weeks) {
        this.studyId = studyId;
        this.weeks = weeks;
    }
}
