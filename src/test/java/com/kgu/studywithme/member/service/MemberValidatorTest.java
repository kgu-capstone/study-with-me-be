package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member [Service Layer] -> MemberValidator 테스트")
class MemberValidatorTest extends ServiceTest {
    @Autowired
    private MemberValidator memberValidator;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("이메일 중복에 대한 검증을 진행한다")
    void checkUniqueEmail() {
        final Email same = member.getEmail();
        final Email diff = Email.from("diff" + member.getEmailValue());

        assertThatThrownBy(() -> memberValidator.validateEmail(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateEmail(diff));
    }

    @Test
    @DisplayName("닉네임 중복에 대한 검증을 진행한다")
    void checkUniqueNickname() {
        final Nickname same = member.getNickname();
        final Nickname diff = Nickname.from("diff" + member.getNicknameValue());

        assertThatThrownBy(() -> memberValidator.validateNickname(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateNickname(diff));
    }

    @Test
    @DisplayName("전화번호 중복에 대한 검증을 진행한다")
    void checkUniquePhone() {
        final String same = member.getPhone();
        final String diff = member.getPhone().replaceAll("0", "9");

        assertThatThrownBy(() -> memberValidator.validatePhone(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        assertDoesNotThrow(() -> memberValidator.validatePhone(diff));
    }
}
