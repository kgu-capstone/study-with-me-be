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

@DisplayName("Member 도메인 {Nickname VO} 테스트")
class NicknameTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"한", "!@#hello", "Hello World", "일이삼사오육칠팔구십십일"})
    @DisplayName("형식에 맞지 않는 닉네임은 예외가 발생한다")
    void throwExceptionByMalformedNickname(String value){
        assertThatThrownBy(() -> Nickname.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_NICKNAME_PATTERN.getMessage());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"HelloWorld", "HelloJava"})
    @DisplayName("닉네임 업데이트에 성공한다")
    void updateNickname(String value){
        // given
        Nickname nickname = Nickname.from("Hello");

        // when
        Nickname updateNickname = nickname.update(value);

        // then
        assertAll(
                () -> assertThat(updateNickname.getValue()).isNotEqualTo(nickname.getValue()),
                () -> assertThat(updateNickname.getValue()).isEqualTo(value)
        );
    }

    @Test
    @DisplayName("이전과 동일한 닉네임인지 검증한다")
    void validateNicknameSameAsBefore() {
        // given
        Nickname nickname = Nickname.from("HelloWorld");
        String compareNickname1 = "HelloWorld";
        String compareNickname2 = "HelloJava";

        // when
        boolean actual1 = nickname.isSameNickname(compareNickname1);
        boolean actual2 = nickname.isSameNickname(compareNickname2);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
