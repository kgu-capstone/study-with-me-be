package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.fixture.WeekFixture;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Region;
import com.kgu.studywithme.member.domain.report.Report;
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
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member [Service Layer] -> MemberService 테스트")
class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("회원가입")
    class signUp {
        @Test
        @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
        void duplicateEmail() {
            // given
            final Member member = memberRepository.save(JIWON.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    member.getEmailValue(),
                    "diff" + member.getNicknameValue(),
                    member.getPhone().replaceAll("0", "9")
            );
            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
        void duplicateNickname() {
            // given
            final Member member = memberRepository.save(JIWON.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    "diff" + member.getEmailValue(),
                    member.getNicknameValue(),
                    member.getPhone().replaceAll("0", "9")
            );
            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
        void duplicatePhone() {
            // given
            final Member member = memberRepository.save(JIWON.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    "diff" + member.getEmailValue(),
                    "diff" + member.getNicknameValue(),
                    member.getPhone()
            );
            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        }

        @Test
        @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
        void success() {
            // given
            final Member member = JIWON.toMember();

            // when
            Long savedMemberId = memberService.signUp(member);

            // then
            Member findMember = memberRepository.findById(member.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findMember.getId()).isEqualTo(savedMemberId),
                    () -> assertThat(findMember.getInterests()).containsAll(JIWON.getInterests())
            );
        }
    }

    @Nested
    @DisplayName("사용자 신고")
    class report {
        private Member reportee;
        private Member reporter;

        @BeforeEach
        void setUp() {
            reportee = memberRepository.save(GHOST.toMember());
            reporter = memberRepository.save(JIWON.toMember());
        }

        @Test
        @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
        void failureByPreviousReportIsStillPending() {
            // given
            memberService.report(reportee.getId(), reporter.getId(), "참여를 안해요");

            // when - then
            assertThatThrownBy(() -> memberService.report(reportee.getId(), reporter.getId(), "10주 연속 미출석입니다"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.REPORT_IS_STILL_RECEIVED.getMessage());
        }

        @Test
        @DisplayName("사용자 신고에 성공한다")
        void success() {
            // given
            final String reason = "참여를 안해요";

            // when
            Long reportId = memberService.report(reportee.getId(), reporter.getId(), reason);

            // then
            Report findReport = reportRepository.findById(reportId).orElseThrow();
            assertAll(
                    () -> assertThat(findReport.getReporteeId()).isEqualTo(reportee.getId()),
                    () -> assertThat(findReport.getReporterId()).isEqualTo(reporter.getId()),
                    () -> assertThat(findReport.getReason()).isEqualTo(reason),
                    () -> assertThat(findReport.getStatus()).isEqualTo(RECEIVE)
            );
        }
    }

    @Nested
    @DisplayName("피어리뷰 등록")
    class writeReview {
        private final String CONTENT = "Very Good!";

        private final Member[] members = new Member[3];
        private Member outsider;

        @BeforeEach
        void setUp() {
            members[0] = memberRepository.save(DUMMY1.toMember());
            members[1] = memberRepository.save(DUMMY2.toMember());
            members[2] = memberRepository.save(DUMMY3.toMember());
            outsider = memberRepository.save(GHOST.toMember());
            Study study = studyRepository.save(TOEIC.toOnlineStudy(members[0]));

            beParticipation(study, members[1]);
            beParticipation(study, members[2]);

            weekRepository.save(WeekFixture.STUDY_WEEKLY_1.toWeekWithAssignment(study));

            attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, members[0]));
            attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, members[1]));
            attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, members[2]));
        }

        @Test
        @DisplayName("해당 사용자에 대해 두 번이상 피어리뷰를 남길 수 없다")
        void alreadyReview() {
            // given
            memberService.writeReview(members[0].getId(), members[1].getId(), CONTENT);

            // when - then
            assertThatThrownBy(() -> memberService.writeReview(members[0].getId(), members[1].getId(), CONTENT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.ALREADY_REVIEW.getMessage());
        }

        @Test
        @DisplayName("본인에게 피어리뷰를 남길 수 없다")
        void selfReviewNotAllowed() {
            assertThatThrownBy(() -> memberService.writeReview(members[0].getId(), members[0].getId(), CONTENT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("함께 스터디를 진행한 기록이 없다면 피어리뷰를 남길 수 없다")
        void commonStudyNotFound() {
            assertThatThrownBy(() -> memberService.writeReview(members[0].getId(), outsider.getId(), "BAD REVIEW"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.COMMON_STUDY_NOT_FOUND.getMessage());

            assertDoesNotThrow(() -> memberService.writeReview(members[0].getId(), members[1].getId(), "GOOD REVIEW"));
            assertDoesNotThrow(() -> memberService.writeReview(members[1].getId(), members[2].getId(), "GOOD REVIEW"));
        }

        @Test
        @DisplayName("피어리뷰 등록에 성공한다")
        void success() {
            // given
            memberService.writeReview(members[0].getId(), members[1].getId(), CONTENT);

            // when
            PeerReview findPeerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(members[0].getId(), members[1].getId()).orElseThrow();

            // then
            assertAll(
                    () -> assertThat(findPeerReview.getReviewee()).isEqualTo(members[0]),
                    () -> assertThat(findPeerReview.getReviewer()).isEqualTo(members[1]),
                    () -> assertThat(findPeerReview.getContent()).isEqualTo(CONTENT)
            );
        }
    }

    @Nested
    @DisplayName("피어리뷰 수정")
    class updateReview {
        private final String CONTENT = "Very Good!";
        private final String NEW_CONTENT = "Bad..";

        private final Member[] members = new Member[3];

        @BeforeEach
        void setUp() {
            members[0] = memberRepository.save(DUMMY1.toMember());
            members[1] = memberRepository.save(DUMMY2.toMember());
            members[2] = memberRepository.save(DUMMY3.toMember());
            Study study = studyRepository.save(TOEIC.toOnlineStudy(members[0]));

            beParticipation(study, members[1]);
            beParticipation(study, members[2]);

            weekRepository.save(WeekFixture.STUDY_WEEKLY_1.toWeekWithAssignment(study));

            attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, members[0]));
            attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, members[1]));
            attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study, members[2]));

            members[0].applyPeerReview(members[1], CONTENT);
        }

        @Test
        @DisplayName("피어리뷰 기록이 없다면 수정할 수 없다")
        void reviewNotFound() {
            assertThatThrownBy(() -> memberService.updateReview(members[0].getId(), members[2].getId(), NEW_CONTENT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.REVIEW_NOT_FOUND.getMessage());

            assertDoesNotThrow(() -> memberService.updateReview(members[0].getId(), members[1].getId(), NEW_CONTENT));

        }

        @Test
        @DisplayName("피어리뷰 수정에 성공한다")
        void success() {
            // given
            memberService.updateReview(members[0].getId(), members[1].getId(), NEW_CONTENT);

            // when
            PeerReview findPeerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(members[0].getId(), members[1].getId()).orElseThrow();

            // then
            assertThat(findPeerReview.getContent()).isEqualTo(NEW_CONTENT);
        }
    }

    private Member createDuplicateMember(String email, String nickname, String phone) {
    return Member.builder()
            .name(JIWON.getName())
            .nickname(Nickname.from(nickname))
            .email(Email.from(email))
            .birth(JIWON.getBirth())
            .phone(phone)
            .gender(JIWON.getGender())
            .region(Region.of(JIWON.getProvince(), JIWON.getCity()))
            .interests(JIWON.getInterests())
            .build();
    }

    private void beParticipation(Study study, Member member) {
        study.applyParticipation(member);
        study.approveParticipation(member);
    }
}
