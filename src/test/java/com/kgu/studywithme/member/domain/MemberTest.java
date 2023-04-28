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
    void constuct() {
        Member member = JIWON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(JIWON.getNickname()),
                () -> assertThat(member.getEmailValue()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(JIWON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(JIWON.getCity()),
                () -> assertThat(member.getScore()).isEqualTo(100),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(JIWON.getInterests())
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
        assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(interests);
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
            // 리뷰 받는 사람
            reviewee1 = JIWON.toMember();
            reviewee2 = GHOST.toMember();

            // 리뷰 작성하는 사람
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
                            .containsExactlyInAnyOrder(reviewer1),
                    () -> assertThat(reviewee1.getPeerReviews())
                            .map(PeerReview::getContent)
                            .containsExactlyInAnyOrder("좋은 팀원이에요."),

                    // reviewee2
                    () -> assertThat(reviewee2.getPeerReviews()).hasSize(2),
                    () -> assertThat(reviewee2.getPeerReviews())
                            .map(PeerReview::getReviewer)
                            .containsExactlyInAnyOrder(reviewer1, reviewer2),
                    () -> assertThat(reviewee2.getPeerReviews())
                            .map(PeerReview::getContent)
                            .containsExactlyInAnyOrder("열심히 안해요.", "열심히 안해요.")
            );
        }
    }

    @Nested
    @DisplayName("사용자 점수 업데이트")
    class updateScore{
        private Member member;

        @BeforeEach
        void setUp() {
            member = JIWON.toMember();
        }

        @Nested
        @DisplayName("단순 출석에 대한 점수 업데이트")
        class applySimpleAttendance {
            @Test
            @DisplayName("출석에 대한 점수를 적용한다")
            void applyAttendance() {
                // given
                member.applyAbsence(); // 100 - 5 = 95

                // when
                member.applyAttendance(); // 95 + 1

                // then
                assertThat(member.getScore()).isEqualTo(96);
            }

            @Test
            @DisplayName("지각에 대한 점수를 적용한다")
            void applyLate() {
                // when
                member.applyLate(); // 100 - 1

                // then
                assertThat(member.getScore()).isEqualTo(99);
            }

            @Test
            @DisplayName("결석에 대한 점수를 적용한다")
            void applyAbsence() {
                // when
                member.applyAbsence(); // 100 - 5

                // then
                assertThat(member.getScore()).isEqualTo(95);
            }
        }

        @Nested
        @DisplayName("이전 출석 정보 수정에 따른 점수 업데이트")
        class applyComplexAttendance {
            @BeforeEach
            void setUp() {
                for (int i = 0; i < 5; i++) {
                    member.applyAbsence();
                } // 75
            }

            @Test
            @DisplayName("출석 -> 지각으로 수정함에 따라 점수를 업데이트한다")
            void updateAttendanceToLate() {
                // when
                member.updateAttendanceToLate(); // 75 - 1 - 1

                // then
                assertThat(member.getScore()).isEqualTo(73);
            }

            @Test
            @DisplayName("출석 -> 결석으로 수정함에 따라 점수를 업데이트한다")
            void updateAttendanceToAbsence() {
                // when
                member.updateAttendanceToAbsence(); // 75 - 1 - 5

                // then
                assertThat(member.getScore()).isEqualTo(69);
            }

            @Test
            @DisplayName("지각 -> 출석으로 수정함에 따라 점수를 업데이트한다")
            void updateLateToAttendance() {
                // when
                member.updateLateToAttendance(); // 75 + 1 + 1

                // then
                assertThat(member.getScore()).isEqualTo(77);
            }

            @Test
            @DisplayName("지각 -> 결석으로 수정함에 따라 점수를 업데이트한다")
            void updateLateToAbsence() {
                // when
                member.updateLateToAbsence(); // 75 + 1 - 5

                // then
                assertThat(member.getScore()).isEqualTo(71);
            }

            @Test
            @DisplayName("결석 -> 출석으로 수정함에 따라 점수를 업데이트한다")
            void updateAbsenceToAttendance() {
                // when
                member.updateAbsenceToAttendance(); // 75 + 5 + 1

                // then
                assertThat(member.getScore()).isEqualTo(81);
            }

            @Test
            @DisplayName("결석 -> 지각으로 수정함에 따라 점수를 업데이트한다")
            void updateAbsenceToLate() {
                // when
                member.updateAbsenceToLate(); // 75 + 5 - 1

                // then
                assertThat(member.getScore()).isEqualTo(79);
            }
        }
    }
}
