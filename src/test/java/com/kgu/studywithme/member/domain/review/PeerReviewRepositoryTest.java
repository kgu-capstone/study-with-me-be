package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member [Repository Layer] -> PeerReviewRepository 테스트")
public class PeerReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private PeerReviewRepository peerReviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member reviewee;
    private final Member[] reviewers = new Member[5];

    @BeforeEach
    void setUp() {
        reviewee = memberRepository.save(JIWON.toMember());
        reviewers[0] = memberRepository.save(DUMMY1.toMember());
        reviewers[1] = memberRepository.save(DUMMY2.toMember());
        reviewers[2] = memberRepository.save(DUMMY3.toMember());
        reviewers[3] = memberRepository.save(DUMMY4.toMember());
        reviewers[4] = memberRepository.save(DUMMY5.toMember());
    }

    @Test
    @DisplayName("사용자의 PeerReview를 조회한다")
    void findContentByRevieweeId() {
        /* 3명 피어리뷰 */
        doReview(reviewers[0], reviewers[1], reviewers[2]);

        List<String> result1 = peerReviewRepository.findPeerReviewByMemberId(reviewee.getId());
        assertThat(result1).hasSize(3);
        assertThat(result1).containsExactly(
                "BEST! - " + reviewers[0].getId(),
                "BEST! - " + reviewers[1].getId(),
                "BEST! - " + reviewers[2].getId()
        );

        /* 추가 2명 피어리뷰 */
        doReview(reviewers[3], reviewers[4]);

        List<String> result2 = peerReviewRepository.findPeerReviewByMemberId(reviewee.getId());
        assertThat(result2).hasSize(5);
        assertThat(result2).containsExactly(
                "BEST! - " + reviewers[0].getId(),
                "BEST! - " + reviewers[1].getId(),
                "BEST! - " + reviewers[2].getId(),
                "BEST! - " + reviewers[3].getId(),
                "BEST! - " + reviewers[4].getId()
        );
    }

    private void doReview(Member... reviewers) {
        for (Member reviewer : reviewers) {
            reviewee.applyPeerReview(reviewer, "BEST! - " + reviewer.getId());
        }
    }
}
