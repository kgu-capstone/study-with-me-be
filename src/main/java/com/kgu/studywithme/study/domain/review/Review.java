package com.kgu.studywithme.study.domain.review;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_review")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
    private Member writer;

    private Review(Study study, Member writer, String content) {
        this.study = study;
        this.writer = writer;
        this.content = content;
    }

    public static Review writeReview(Study study, Member writer, String content) {
        return new Review(study, writer, content);
    }

    public boolean isSameMember(Member other) {
        return this.writer.isSameMember(other);
    }

    public void updateReview(String content) {
        this.content = content;
    }
}
