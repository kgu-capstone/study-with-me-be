package com.kgu.studywithme.study.domain.review;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Review 도메인 {Reviews VO} 테스트")
class ReviewsTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);

    @Nested
    @DisplayName("스터디 리뷰 작성")
    class writeReview {
        @Test
        @DisplayName("이전에 이미 리뷰를 작성했다면 중복으로 리뷰를 작성할 수 없다")
        void throwExceptionByAlreadyReviewWritten() {
            // given
            Reviews reviews = Reviews.createReviewsPage();
            reviews.writeReview(Review.writeReview(STUDY, PARTICIPANT, "리뷰 1"));

            // when - then
            assertThatThrownBy(() -> reviews.writeReview(Review.writeReview(STUDY, PARTICIPANT, "리뷰 2")))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_REVIEW_WRITTEN.getMessage());
        }

        @Test
        @DisplayName("리뷰 작성에 성공한다")
        void success() {
            // given
            Reviews reviews = Reviews.createReviewsPage();

            // when
            reviews.writeReview(Review.writeReview(STUDY, PARTICIPANT, "리뷰 1"));

            // then
            assertAll(
                    () -> assertThat(reviews.getReviews()).hasSize(1),
                    () -> assertThat(reviews.getReviews())
                            .map(Review::getContent)
                            .containsExactlyInAnyOrder("리뷰 1")
            );
        }
    }
}
