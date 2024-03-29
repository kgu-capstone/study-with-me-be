package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 도메인 {Nickname VO} 테스트")
class NicknameTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"한", "!@#hello", "Hello World", "일이삼사오육칠팔구십십일"})
    @DisplayName("형식에 맞지 않는 Nickname이면 생성에 실패한다")
    void throwExceptionByInvalidNicknameFormat(String value){
        assertThatThrownBy(() -> Nickname.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_NICKNAME_FORMAT.getMessage());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"하이", "하이123", "hEllo123"})
    @DisplayName("Nickname을 생성한다")
    void construct(String value) {
        Nickname nickname = Nickname.from(value);

        assertThat(nickname.getValue()).isEqualTo(value);
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
        assertThat(updateNickname.getValue()).isEqualTo(value);
    }
}
