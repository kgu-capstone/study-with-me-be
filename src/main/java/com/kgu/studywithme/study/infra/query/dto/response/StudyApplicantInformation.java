package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Score;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StudyApplicantInformation {
    private final Long id;
    private final String nickname;
    private final int score;
    private final LocalDateTime applyDate;

    @QueryProjection
    public StudyApplicantInformation(Long id, Nickname nickname, Score score, LocalDateTime applyDate) {
        this.id = id;
        this.nickname = nickname.getValue();
        this.score = score.getValue();
        this.applyDate = applyDate;
    }
}
