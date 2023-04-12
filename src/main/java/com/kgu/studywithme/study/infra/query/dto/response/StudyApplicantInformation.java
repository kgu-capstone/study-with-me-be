package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StudyApplicantInformation {
    private final Long id;
    private final String nickname;
    private final LocalDateTime applyDate;

    @QueryProjection
    public StudyApplicantInformation(Long id, Nickname nickname, LocalDateTime applyDate) {
        this.id = id;
        this.nickname = nickname.getValue();
        this.applyDate = applyDate;
    }
}
