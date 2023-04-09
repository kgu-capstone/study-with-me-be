package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    @Test
    @DisplayName("Member를 생성한다")
    void createMember() {
        Member member = JIWON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(JIWON.getNickname()),
                () -> assertThat(member.getEmailValue()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(JIWON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(JIWON.getCity()),
                () -> assertThat(member.getInterests()).containsAll(JIWON.getInterests())
        );
    }

    @Test
    @DisplayName("Member의 관심사를 수정한다")
    void updateInterests() {
        // given
        Member member = JIWON.toMember();

        // when
        final Set<Category> interests = Set.of(APTITUDE_NCS, CERTIFICATION, ETC);
        member.applyInterests(interests);

        // then
        assertThat(member.getInterests()).containsAll(interests);
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() {
        // given
        Member member = JIWON.toMember();

        // when
        final String change = JIWON.getNickname() + "diff";
        member.changeNickname(change);

        // then
        assertThat(member.getNicknameValue()).isEqualTo(change);
    }

    @Test
    @DisplayName("이메일을 통해서 동일한 사용자인지 확인한다")
    void isSameMember() {
        // given
        final Member member = JIWON.toMember();
        Member compare1 = JIWON.toMember();
        Member compare2 = GHOST.toMember();

        // when
        boolean actual1 = member.isSameMember(compare1);
        boolean actual2 = member.isSameMember(compare2);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Nested
    @DisplayName("스터디 피어 리뷰 작성")
    class applyPeerReview {
        private Member reviewee1;
        private Member reviewee2;
        private Member reviewer1;
        private Member reviewer2;

        @BeforeEach
        void setUp() {
            reviewee1 = JIWON.toMember();
            reviewee2 = GHOST.toMember();

            reviewer1 = DUMMY1.toMember();
            reviewer2 = DUMMY2.toMember();
        }

        @Test
        @DisplayName("이미 리뷰를 작성했다면 중복으로 작성할 수 없다")
        void failureByAlreadyReview() {
            // given
            reviewee1.applyPeerReview(reviewer1, "좋은 팀원이에요.");

            // when - then
            assertThatThrownBy(() -> reviewee1.applyPeerReview(reviewer1, "열심히 안해요."))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.ALREADY_REVIEW.getMessage());
        }

        @Test
        @DisplayName("피어 리뷰 작성에 성공한다")
        void success() {
            // when
            /* reviewer1 -> reviewee1 */
            reviewee1.applyPeerReview(reviewer1, "좋은 팀원이에요.");

            /* reviewer1, reviewer2 -> reviewee2 */
            reviewee2.applyPeerReview(reviewer1, "열심히 안해요.");
            reviewee2.applyPeerReview(reviewer2, "열심히 안해요.");

            // then
            assertAll(
                    // reviewee1
                    () -> assertThat(reviewee1.getPeerReviews()).hasSize(1),
                    () -> assertThat(reviewee1.getPeerReviews())
                            .map(PeerReview::getReviewer)
                            .containsExactly(reviewer1),
                    () -> assertThat(reviewee1.getPeerReviews())
                            .map(PeerReview::getContent)
                            .containsExactly("좋은 팀원이에요."),

                    // reviewee2
                    () -> assertThat(reviewee2.getPeerReviews()).hasSize(2),
                    () -> assertThat(reviewee2.getPeerReviews())
                            .map(PeerReview::getReviewer)
                            .containsExactly(reviewer1, reviewer2),
                    () -> assertThat(reviewee2.getPeerReviews())
                            .map(PeerReview::getContent)
                            .containsExactly("열심히 안해요.", "열심히 안해요.")
            );
        }
    }
}
