package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member [Service Layer] -> MemberReviewService 테스트")
class MemberReviewServiceTest extends ServiceTest {
    @Autowired
    private MemberReviewService memberReviewService;

    private Member host;
    private final Member[] participants = new Member[3];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        participants[0] = memberRepository.save(DUMMY1.toMember());
        participants[1] = memberRepository.save(DUMMY2.toMember());
        participants[2] = memberRepository.save(DUMMY3.toMember());

        Study study = studyRepository.save(TOEIC.toOnlineStudy(host));
        beParticipation(study, participants[0], participants[1], participants[2]);

        weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, host));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, participants[0]));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, participants[1]));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, participants[2]));
    }

    @Nested
    @DisplayName("피어리뷰 등록")
    class writeReview {
        @Test
        @DisplayName("해당 사용자에 대해 두 번이상 피어리뷰를 남길 수 없다")
        void alreadyReview() {
            // given
            memberReviewService.writeReview(participants[0].getId(), participants[1].getId(), "GOOD-1");

            // when - then
            assertThatThrownBy(() -> memberReviewService.writeReview(participants[0].getId(), participants[1].getId(), "GOOD-2"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.ALREADY_REVIEW.getMessage());
        }

        @Test
        @DisplayName("본인에게 피어리뷰를 남길 수 없다")
        void selfReviewNotAllowed() {
            assertThatThrownBy(() -> memberReviewService.writeReview(participants[0].getId(), participants[0].getId(), "GOOD"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("함께 스터디를 진행한 기록이 없다면 피어리뷰를 남길 수 없다")
        void commonStudyNotFound() {
            // given
            Member outsider = memberRepository.save(GHOST.toMember());

            // when - then
            assertThatThrownBy(() -> memberReviewService.writeReview(participants[0].getId(), outsider.getId(), "BAD"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.COMMON_STUDY_NOT_FOUND.getMessage());
            assertThatThrownBy(() -> memberReviewService.writeReview(outsider.getId(), participants[0].getId(), "BAD"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.COMMON_STUDY_NOT_FOUND.getMessage());

            assertDoesNotThrow(() -> memberReviewService.writeReview(host.getId(), participants[1].getId(), "GOOD"));
            assertDoesNotThrow(() -> memberReviewService.writeReview(participants[1].getId(), host.getId(), "GOOD"));
        }

        @Test
        @DisplayName("피어리뷰 등록에 성공한다")
        void success() {
            // when
            memberReviewService.writeReview(host.getId(), participants[0].getId(), "GOOD-" + participants[0].getId());
            memberReviewService.writeReview(host.getId(), participants[1].getId(), "GOOD-" + participants[1].getId());
            memberReviewService.writeReview(participants[0].getId(), participants[2].getId(), "GOOD-" + participants[2].getId());
            memberReviewService.writeReview(participants[1].getId(), participants[2].getId(), "GOOD-" + participants[2].getId());

            // then
            assertThatWriteReviewMatch(host, participants[0], participants[1]);
            assertThatWriteReviewMatch(participants[0], participants[2]);
            assertThatWriteReviewMatch(participants[1], participants[2]);
        }

        private void assertThatWriteReviewMatch(Member reviewee, Member... reviewers) {
            for (Member reviewer : reviewers) {
                PeerReview peerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(reviewee.getId(), reviewer.getId())
                        .orElseThrow();

                assertAll(
                        () -> assertThat(peerReview.getReviewee()).isEqualTo(reviewee),
                        () -> assertThat(peerReview.getReviewer()).isEqualTo(reviewer),
                        () -> assertThat(peerReview.getContent()).isEqualTo("GOOD-" + reviewer.getId())
                );
            }
        }
    }

    @Nested
    @DisplayName("피어리뷰 수정")
    class updateReview {
        private final String UPDATE_CONTENT = "VERY GOOD";

        @BeforeEach
        void setUp() {
            host.applyPeerReview(participants[0], "GOOD");
        }

        @Test
        @DisplayName("피어리뷰 기록이 없다면 수정할 수 없다")
        void reviewNotFound() {
            assertThatThrownBy(() -> memberReviewService.updateReview(host.getId(), participants[1].getId(), UPDATE_CONTENT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.REVIEW_NOT_FOUND.getMessage());

            assertDoesNotThrow(() -> memberReviewService.updateReview(host.getId(), participants[0].getId(), UPDATE_CONTENT));
        }

        @Test
        @DisplayName("피어리뷰 수정에 성공한다")
        void success() {
            // when
            memberReviewService.updateReview(host.getId(), participants[0].getId(), UPDATE_CONTENT);

            // then
            PeerReview findPeerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(host.getId(), participants[0].getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findPeerReview.getReviewee()).isEqualTo(host),
                    () -> assertThat(findPeerReview.getReviewer()).isEqualTo(participants[0]),
                    () -> assertThat(findPeerReview.getContent()).isEqualTo(UPDATE_CONTENT)
            );
        }
    }

    private void beParticipation(Study study, Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }
}
