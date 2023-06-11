package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.study.service.dto.response.StudyMember;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewInformation {
    private final Long id;
    private final String content;
    private final LocalDateTime reviewDate;
    private final StudyMember reviewer;

    @QueryProjection
    public ReviewInformation(Long id, String content, LocalDateTime reviewDate, Long reviewerId, Nickname reviewerNickname) {
        this.id = id;
        this.content = content;
        this.reviewDate = reviewDate;
        this.reviewer = new StudyMember(reviewerId, reviewerNickname.getValue());
    }
}
