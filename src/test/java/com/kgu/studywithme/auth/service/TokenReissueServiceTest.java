package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> TokenReissueService 테스트")
class TokenReissueServiceTest extends ServiceTest {
    @Autowired
    private TokenReissueService tokenReissueService;

    private Long memberId;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        refreshToken = jwtTokenProvider.createRefreshToken(memberId);
    }

    @Nested
    @DisplayName("토큰 재발급")
    class reissueTokens {
        @Test
        @DisplayName("RefreshToken이 유효하지 않으면 토큰 재발급에 실패한다")
        void throwExceptionByInvalidRefreshToken() {
            // when - then
            assertThatThrownBy(() -> tokenReissueService.reissueTokens(memberId, refreshToken))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.AUTH_INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("유효성이 확인된 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void reissueSuccess() {
            // given
            tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

            // when
            TokenResponse response = tokenReissueService.reissueTokens(memberId, refreshToken);

            // then
            assertAll(
                    () -> assertThat(response).isNotNull(),
                    () -> assertThat(response)
                            .usingRecursiveComparison()
                            .isNotNull()
            );
        }
    }
}
