package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_review")
public class PeerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewee_id", referencedColumnName = "id", nullable = false)
    private Member reviewee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", referencedColumnName = "id", nullable = false)
    private Member reviewer;

    @Builder
    private PeerReview(Member reviewee, Member reviewer, String content) {
        this.reviewee = reviewee;
        this.reviewer = reviewer;
        this.content = content;
    }

    public static PeerReview doReview(Member reviewee, Member reviewer, String content) {
        return new PeerReview(reviewee, reviewer, content);
    }

    public void updateReview(String content) {
        this.content = content;
    }
}
