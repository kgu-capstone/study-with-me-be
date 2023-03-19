package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 {Email VO} 테스트")
class EmailTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"test1@gmail.com", "test2@gmail.com"})
    @DisplayName("Email을 생성한다")
    void constructSuccess(String value) {
        Email email = Email.from(value);
        assertThat(email.getValue()).isEqualTo(value);
    }
    
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@naver.com", "abc@kakao.com"})
    @DisplayName("형식에 맞지 않는 Email이면 생성에 실패한다")
    void constructFailure(String value) {
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL.getMessage());
    }

    @Test
    @DisplayName("동일한 이메일인지 확인한다")
    void isSameEmail() {
        // given
        final Email email = Email.from("test1@gmail.com");
        Email compare1 = Email.from("test1@gmail.com");
        Email compare2 = Email.from("test2@gmail.com");

        // when
        boolean actual1 = email.isSameEmail(compare1);
        boolean actual2 = email.isSameEmail(compare2);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
