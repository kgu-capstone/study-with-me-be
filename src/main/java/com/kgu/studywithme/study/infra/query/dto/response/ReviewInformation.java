package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.study.service.dto.response.StudyMember;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewInformation {
    private final StudyMember reviewer;
    private final String content;
    private final LocalDateTime reviewDate;

    @Builder
    @QueryProjection
    public ReviewInformation(Long reviewerId, Nickname reviewerNickname, String content, LocalDateTime reviewDate) {
        this.reviewer = new StudyMember(reviewerId, reviewerNickname.getValue());
        this.content = content;
        this.reviewDate = reviewDate;
    }
}
