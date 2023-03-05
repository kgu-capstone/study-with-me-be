package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    @Test
    @DisplayName("멤버를 생성한다")
    void createMember() {
        Member member = SEO_JI_WON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(SEO_JI_WON.getName()),
                () -> assertThat(member.getEmailValue()).isEqualTo(SEO_JI_WON.getEmail()),
                () -> assertThat(ENCODER.matches(SEO_JI_WON.getPassword(), member.getPasswordValue())).isTrue(),
                () -> assertThat(member.getBirth()).isEqualTo(SEO_JI_WON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(SEO_JI_WON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(SEO_JI_WON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(SEO_JI_WON.getCity())
        );
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void changePassword() {
        // given
        Member member = SEO_JI_WON.toMember();
        final String same = SEO_JI_WON.getPassword();
        final String diff = SEO_JI_WON.getPassword() + "diff";

        // when
        assertThatThrownBy(() -> member.changePassword(same, ENCODER))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());

        member.changePassword(diff, ENCODER);

        // then
        assertThat(ENCODER.matches(diff, member.getPasswordValue())).isTrue();
    }
}
