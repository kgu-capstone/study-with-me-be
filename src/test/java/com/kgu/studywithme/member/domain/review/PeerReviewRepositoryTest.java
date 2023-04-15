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
import static org.junit.jupiter.api.Assertions.assertAll;

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
    void findPeerReviewByMemberId() {
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

    @Test
    @DisplayName("리뷰 대상자 ID와 리뷰 작성자 ID로 리뷰가 존재하는지 확인한다")
    void existsByRevieweeIdAndReviewerId() {
        doReview(reviewers[0], reviewers[1], reviewers[2]);

        assertAll(
                () -> assertThat(peerReviewRepository.existsByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[0].getId())).isTrue(),
                () -> assertThat(peerReviewRepository.existsByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[1].getId())).isTrue(),
                () -> assertThat(peerReviewRepository.existsByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[2].getId())).isTrue(),
                () -> assertThat(peerReviewRepository.existsByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[3].getId())).isFalse(),
                () -> assertThat(peerReviewRepository.existsByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[4].getId())).isFalse()
        );
    }

    @Test
    @DisplayName("리뷰 대상자 ID와 리뷰 작성자 ID로 리뷰를 조회한다")
    void findByRevieweeIdAndReviewerId() {
        doReview(reviewers[0], reviewers[1], reviewers[2], reviewers[3], reviewers[4]);

        PeerReview[] findPeerReviews = new PeerReview[5];
        findPeerReviews[0] = peerReviewRepository.findByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[0].getId()).orElseThrow();
        findPeerReviews[1] = peerReviewRepository.findByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[1].getId()).orElseThrow();
        findPeerReviews[2] = peerReviewRepository.findByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[2].getId()).orElseThrow();
        findPeerReviews[3] = peerReviewRepository.findByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[3].getId()).orElseThrow();
        findPeerReviews[4] = peerReviewRepository.findByRevieweeIdAndReviewerId(reviewee.getId(), reviewers[4].getId()).orElseThrow();

        assertAll(
                () -> assertThat(findPeerReviews[0].getContent()).isEqualTo("BEST! - " + reviewers[0].getId()),
                () -> assertThat(findPeerReviews[1].getContent()).isEqualTo("BEST! - " + reviewers[1].getId()),
                () -> assertThat(findPeerReviews[2].getContent()).isEqualTo("BEST! - " + reviewers[2].getId()),
                () -> assertThat(findPeerReviews[3].getContent()).isEqualTo("BEST! - " + reviewers[3].getId()),
                () -> assertThat(findPeerReviews[4].getContent()).isEqualTo("BEST! - " + reviewers[4].getId())
        );
    }

    private void doReview(Member... reviewers) {
        for (Member reviewer : reviewers) {
            reviewee.applyPeerReview(reviewer, "BEST! - " + reviewer.getId());
        }
    }
}
