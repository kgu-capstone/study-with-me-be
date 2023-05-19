package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.controller.dto.request.MemberUpdateRequest;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Region;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberService 테스트")
class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("회원가입")
    class signUp {
        @Test
        @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
        void throwExceptionByDuplicateEmail() {
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
        void throwExceptionByDuplicateNickname() {
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
        void throwExceptionByDuplicatePhone() {
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
                    () -> assertThat(findMember.getInterests()).containsExactlyInAnyOrderElementsOf(JIWON.getInterests())
            );
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정")
    class update {
        private Member member;
        private Member compare;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(JIWON.toMember());
            compare = memberRepository.save(GHOST.toMember());
        }

        @Test
        @DisplayName("다른 사람이 사용하고 있는 닉네임으로 수정할 수 없다")
        void throwExceptionByDuplicateNickname() {
            final MemberUpdateRequest request = new MemberUpdateRequest(
                    compare.getNicknameValue(),
                    member.getPhone(),
                    member.getRegionProvince(),
                    member.getRegionCity(),
                    member.isEmailOptIn(),
                    Set.of(INTERVIEW.getId(), PROGRAMMING.getId())
            );

            assertThatThrownBy(() -> memberService.update(member.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("다른 사람이 사용하고 있는 전화번호로 수정할 수 없다")
        void throwExceptionByDuplicatePhone() {
            final MemberUpdateRequest request = new MemberUpdateRequest(
                    member.getNicknameValue(),
                    compare.getPhone(),
                    member.getRegionProvince(),
                    member.getRegionCity(),
                    member.isEmailOptIn(),
                    Set.of(INTERVIEW.getId(), PROGRAMMING.getId())
            );

            assertThatThrownBy(() -> memberService.update(member.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        }

        @Test
        @DisplayName("사용자 정보를 수정한다")
        void success() {
            // given
            final MemberUpdateRequest request = new MemberUpdateRequest(
                    "updateNick",
                    "01012300593",
                    "경기도",
                    "성남시",
                    false,
                    Set.of(INTERVIEW.getId(), PROGRAMMING.getId())
            );

            // when
            memberService.update(member.getId(), request);

            // then
            assertAll(
                    () -> assertThat(member.getNicknameValue()).isEqualTo("updateNick"),
                    () -> assertThat(member.getPhone()).isEqualTo("01012300593"),
                    () -> assertThat(member.getRegionProvince()).isEqualTo("경기도"),
                    () -> assertThat(member.getRegionCity()).isEqualTo("성남시"),
                    () -> assertThat(member.isEmailOptIn()).isFalse(),
                    () -> assertThat(member.getInterests()).containsExactlyInAnyOrder(INTERVIEW, PROGRAMMING)
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
        void throwExceptionByPreviousReportIsStillPending() {
            // given
            memberService.report(reportee.getId(), reporter.getId(), "참여를 안해요");

            // when - then
            assertThatThrownBy(() -> memberService.report(reportee.getId(), reporter.getId(), "10주 연속 미출석입니다"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING.getMessage());
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

    private Member createDuplicateMember(String email, String nickname, String phone) {
        return Member.createMember(
                JIWON.getName(),
                Nickname.from(nickname),
                Email.from(email),
                JIWON.getBirth(),
                phone,
                JIWON.getGender(),
                Region.of(JIWON.getProvince(), JIWON.getCity()),
                JIWON.isEmailOptIn(),
                JIWON.getInterests()
        );
    }
}
