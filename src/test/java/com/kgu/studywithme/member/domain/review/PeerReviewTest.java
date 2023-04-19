package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member-PeerReview 도메인 테스트")
class PeerReviewTest {
    @Test
    @DisplayName("사용자 리뷰를 작성한다")
    void construct() {
        // given
        final Member jiwon = JIWON.toMember();
        final Member ghost = GHOST.toMember();
        final String review = "Good!!";

        // when
        PeerReview peerReview = PeerReview.doReview(jiwon, ghost, review);

        // then
        assertAll(
                () -> assertThat(peerReview.getReviewee()).isEqualTo(jiwon),
                () -> assertThat(peerReview.getReviewer()).isEqualTo(ghost),
                () -> assertThat(peerReview.getContent()).isEqualTo(review)
        );
    }

    @Test
    @DisplayName("사용자 리뷰를 수정한다")
    void updateReview() {
        // given
        final Member jiwon = JIWON.toMember();
        final Member ghost = GHOST.toMember();
        PeerReview peerReview = PeerReview.doReview(jiwon, ghost, "Good!!");

        // when
        final String update = "Bad..";
        peerReview.updateReview(update);

        // then
        assertAll(
                () -> assertThat(peerReview.getReviewee()).isEqualTo(jiwon),
                () -> assertThat(peerReview.getReviewer()).isEqualTo(ghost),
                () -> assertThat(peerReview.getContent()).isEqualTo(update)
        );
    }
}
