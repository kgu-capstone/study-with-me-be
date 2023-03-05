package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 {Password VO} 테스트")
class PasswordTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "aaabbbcccdddeeeAAABBBCCCDDDEEE123123123!@#$@!#%!@%!@#$!@#"})
    @DisplayName("형식에 맞지 않는 비밀번호는 예외가 발생한다")
    void throwExceptionByMalformedPassword(String value){
        assertThatThrownBy(() -> Password.encrypt(value, ENCODER))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_PASSWORD_PATTERN.getMessage());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"ABCabc!@#123", "DJKAqwe!@#a1"})
    @DisplayName("비밀번호 업데이트에 성공한다")
    void updatePassword(String value){
        // given
        Password password = Password.encrypt("abcABC123!@#", ENCODER);

        // when
        Password updatePassword = password.update(value, ENCODER);

        // then
        assertThat(ENCODER.matches(value, updatePassword.getValue())).isTrue();
    }

    @Test
    @DisplayName("이전과 동일한 비밀번호인지 검증한다")
    void validatePasswordSameAsBefore() {
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
