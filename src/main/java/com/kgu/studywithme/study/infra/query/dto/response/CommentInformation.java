package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.study.service.dto.response.StudyMember;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentInformation {
    private final Long id;
    private final Long noticeId;
    private final String content;
    private final LocalDateTime writeDate;
    private final StudyMember writer;

    @QueryProjection
    public CommentInformation(Long id, Long noticeId, String content, LocalDateTime writeDate, Long writerId, Nickname writerNickname) {
        this.id = id;
        this.noticeId = noticeId;
        this.content = content;
        this.writeDate = writeDate;
        this.writer = new StudyMember(writerId, writerNickname.getValue());
    }
}
