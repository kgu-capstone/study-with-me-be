package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;
import static com.kgu.studywithme.fixture.MemberFixture.지원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberTest {
    @Test
    void 멤버를_생성한다() {
        Member member = 지원.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(지원.getName()),
                () -> assertThat(member.getEmailValue()).isEqualTo(지원.getEmail()),
                () -> assertThat(ENCODER.matches(지원.getPassword(), member.getPasswordValue())).isTrue(),
                () -> assertThat(member.getBirth()).isEqualTo(지원.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(지원.getGender()),
                () -> assertThat(member.getLocation()).isEqualTo(지원.getLocation())
        );
    }

    @Test
    void 비밀번호를_변경한다() {
        // given
        Member member = 지원.toMember();
        final String same = 지원.getPassword();
        final String diff = 지원.getPassword() + "diff";

        // when
        assertThatThrownBy(() -> member.changePassword(same, ENCODER))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());

        member.changePassword(diff, ENCODER);

        // then
        assertThat(ENCODER.matches(diff, member.getPasswordValue())).isTrue();
    }
}