package com.kgu.studywithme.study.domain.review;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Review 도메인 테스트")
class ReviewTest {
    @Test
    @DisplayName("스터디에 대한 리뷰를 작성한다")
    void construct() {
        // given
        final Study study = SPRING.toOnlineStudy(JIWON.toMember());
        final Member member = GHOST.toMember();

        // when
        Review review = Review.writeReview(study, member, "좋은 스터디");

        // then
        assertAll(
                () -> assertThat(review.getStudy()).isEqualTo(study),
                () -> assertThat(review.getWriter()).isEqualTo(member),
                () -> assertThat(review.getContent()).isEqualTo("좋은 스터디")
        );
    }

    @ParameterizedTest
    @MethodSource("provideForIsSameReviewer")
    @DisplayName("리뷰 작성자인지 확인한다")
    void isSameReviewer(Member member, Member target, boolean expected) {
        Study study = SPRING.toOnlineStudy(JIWON.toMember());
        Review review = Review.writeReview(study, member, "좋은 스터디");

        assertThat(review.isSameMember(target)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsSameReviewer() {
        return Stream.of(
                Arguments.of(JIWON.toMember(), JIWON.toMember(), true),
                Arguments.of(JIWON.toMember(), GHOST.toMember(), false)
        );
    }

    @Test
    @DisplayName("리뷰를 수정한다")
    void updateReview() {
        // given
        final Study study = SPRING.toOnlineStudy(JIWON.toMember());
        final Member member = GHOST.toMember();
        Review review = Review.writeReview(study, member, "좋은 스터디");

        // when
        final String update = "좋은 스터디222";
        review.updateReview(update);

        // then
        assertThat(review.getContent()).isEqualTo(update);
    }
}
