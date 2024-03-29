package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.study.service.dto.response.StudyMember;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeInformation {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final StudyMember writer;
    private List<CommentInformation> comments;

    @QueryProjection
    public NoticeInformation(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt,
                             Long writerId, Nickname writerNickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = new StudyMember(writerId, writerNickname.getValue());
    }

    public void applyComments(List<CommentInformation> comments) {
        this.comments = comments;
    }
}
