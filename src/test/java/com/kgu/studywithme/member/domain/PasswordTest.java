package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 {Password VO} 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class PasswordTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "aaabbbcccdddeeeAAABBBCCCDDDEEE123123123!@#$@!#%!@%!@#$!@#"})
    void 형식에_맞지_않는_비밀번호는_예외가_발생한다(String value){
        assertThatThrownBy(() -> Password.encrypt(value, ENCODER))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_PASSWORD_PATTERN.getMessage());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"ABCabc!@#123", "DJKAqwe!@#a1"})
    void 비밀번호_업데이트에_성공한다(String value){
        // given
        Password password = Password.encrypt("abcABC123!@#", ENCODER);

        // when
        Password updatePassword = password.update(value, ENCODER);

        // then
        assertThat(ENCODER.matches(value, updatePassword.getValue())).isTrue();
    }

    @Test
    void 이전과_동일한_비밀번호인지_검증한다() {
        // given
        Password password = Password.encrypt("abcABC123!@#", ENCODER);
        String comparePassword1 = "abcABC123!@#";
        String comparePassword2 = "abcABC1234!@#";

        // when
        boolean actual1 = password.isSamePassword(comparePassword1, ENCODER);
        boolean actual2 = password.isSamePassword(comparePassword2, ENCODER);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
