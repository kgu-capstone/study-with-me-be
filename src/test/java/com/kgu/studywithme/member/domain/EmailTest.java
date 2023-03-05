package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 도메인 {Email VO} 테스트")
class EmailTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@naver.com", "abc@kakao.com"})
    @DisplayName("형식에 맞지 않는 이메일이면 예외가 발생한다")
    void throwExceptionByMalformedEmail(String value) {
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL_PATTERN.getMessage());
    }
}
