package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.*;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberSignupService 테스트")
class MemberSignupServiceTest extends ServiceTest {
    @Autowired
    private MemberSignupService memberSignupService;

    private static final List<Long> CATEGORIES = List.of(PROGRAMMING.getId(), INTERVIEW.getId(), LANGUAGE.getId());

    @Nested
    @DisplayName("회원가입을 진행할 때 ")
    class Signup {
        @Test
        @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
        void duplicateEmail() {
            // given
            final Member member = SEO_JI_WON.toMember();
            memberRepository.save(member);

            // when - then
            final Member newMember = createMember(
                    member.getEmailValue(),
                    "diff" + member.getNicknameValue(),
                    member.getPhone().replaceAll("0", "9")
            );
            assertThatThrownBy(() -> memberSignupService.signUp(newMember, CATEGORIES))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
        void duplicateNickname() {
            // given
            final Member member = SEO_JI_WON.toMember();
            memberRepository.save(member);

            // when - then
            final Member newMember = createMember(
                    "diff" + member.getEmailValue(),
                    member.getNicknameValue(),
                    member.getPhone().replaceAll("0", "9")
            );
            assertThatThrownBy(() -> memberSignupService.signUp(newMember, CATEGORIES))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
        void duplicatePhone() {
            // given
            final Member member = SEO_JI_WON.toMember();
            memberRepository.save(member);

            // when - then
            final Member newMember = createMember(
                    "diff" + member.getEmailValue(),
                    "diff" + member.getNicknameValue(),
                    member.getPhone()
            );
            assertThatThrownBy(() -> memberSignupService.signUp(newMember, CATEGORIES))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        }

        @Test
        @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
        void success() {
            // given
            final Member member = SEO_JI_WON.toMember();

            // when
            Long savedMemberId = memberSignupService.signUp(member, CATEGORIES);

            // then
            Member findMember = memberRepository.findById(member.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findMember.getId()).isEqualTo(savedMemberId),
                    () -> assertThat(findMember.getInterests()).contains(PROGRAMMING, INTERVIEW, LANGUAGE)
            );
        }
    }

    private Member createMember(String email, String nickname, String phone) {
        return Member.builder()
                .name(SEO_JI_WON.getName())
                .nickname(Nickname.from(nickname))
                .email(Email.from(email))
                .password(Password.encrypt("abcABC123!@#", ENCODER))
                .birth(SEO_JI_WON.getBirth())
                .phone(phone)
                .gender(SEO_JI_WON.getGender())
                .region(Region.of(SEO_JI_WON.getProvince(), SEO_JI_WON.getCity()))
                .build();
    }
}
