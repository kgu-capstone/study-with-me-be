package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Region;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberSignupService 테스트")
class MemberSignUpServiceTest extends ServiceTest {
    @Autowired
    private MemberSignUpService memberSignupService;

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
            assertThatThrownBy(() -> memberSignupService.signUp(newMember))
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
            assertThatThrownBy(() -> memberSignupService.signUp(newMember))
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
            assertThatThrownBy(() -> memberSignupService.signUp(newMember))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        }

        @Test
        @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
        void success() {
            // given
            final Member member = JIWON.toMember();

            // when
            Long savedMemberId = memberSignupService.signUp(member);

            // then
            Member findMember = memberRepository.findById(member.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findMember.getId()).isEqualTo(savedMemberId),
                    () -> assertThat(findMember.getInterests()).containsAll(JIWON.getInterests())
            );
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
}
