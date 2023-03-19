package com.kgu.studywithme.study.domain.notice.comment;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.notice.Notice;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_id", referencedColumnName = "id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
    private Member writer;

    @Builder
    private Comment(Notice notice, Member writer, String content) {
        this.notice = notice;
        this.writer = writer;
        this.content = content;
    }

    public static Comment writeComment(Notice notice, Member writer, String content) {
        return new Comment(notice, writer, content);
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
