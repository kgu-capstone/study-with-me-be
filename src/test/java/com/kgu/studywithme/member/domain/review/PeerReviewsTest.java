package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member-PeerReview 도메인 {PeerReviews VO} 테스트")
class PeerReviewsTest {
    private static final Member REVIEWEE = JIWON.toMember();
    private static final Member REVIEWER = GHOST.toMember();

    @Nested
    @DisplayName("사용자 피어 리뷰 작성")
    class writeReview {
        @Test
        @DisplayName("이전에 이미 리뷰를 작성했다면 중복으로 리뷰를 작성할 수 없다")
        void throwExceptionByAlreadyReview() {
            // given
            PeerReviews reviews = PeerReviews.createPeerReviewsPage();
            reviews.writeReview(PeerReview.doReview(REVIEWEE, REVIEWER, "열심히 하시는 팀원이에요."));

            // when - then
            assertThatThrownBy(() -> reviews.writeReview(PeerReview.doReview(REVIEWEE, REVIEWER, "별로 열심히 안하는 팀원이에요.")))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.ALREADY_REVIEW.getMessage());
        }

        @Test
        @DisplayName("리뷰 작성에 성공한다")
        void success() {
            // given
            PeerReviews reviews = PeerReviews.createPeerReviewsPage();

            // when
            reviews.writeReview(PeerReview.doReview(REVIEWEE, REVIEWER, "열심히 하시는 팀원이에요."));

            // then
            assertAll(
                    () -> assertThat(reviews.getPeerReviews()).hasSize(1),
                    () -> assertThat(reviews.getPeerReviews())
                            .map(PeerReview::getContent)
                            .containsExactly("열심히 하시는 팀원이에요.")
            );
        }
    }
}
