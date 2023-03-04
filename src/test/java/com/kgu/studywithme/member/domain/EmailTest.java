package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 도메인 {Email VO} 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class EmailTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@naver.com", "abc@kakao.com"})
    void 형식에_맞지_않는_이메일이면_예외가_발생한다(String value) {
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL_PATTERN.getMessage());
    }
}
