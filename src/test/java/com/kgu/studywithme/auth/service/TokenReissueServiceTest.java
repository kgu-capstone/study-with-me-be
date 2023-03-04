package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> TokenReissueService 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TokenReissueServiceTest extends ServiceTest {
    @Autowired
    private TokenReissueService tokenReissueService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("토큰 재발급")
    class reissueTokens {
        @Test
        void RefreshToken이_유효하지_않으면_예외가_발생한다() {
            // given
            final Long memberId = 1L;
            final String refreshToken = jwtTokenProvider.createRefreshToken(memberId); // DB에 저장하지 않음에 따라 유효하지 않은 토큰

            // when - then
            assertThatThrownBy(() -> tokenReissueService.reissueTokens(memberId, refreshToken))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.AUTH_INVALID_TOKEN.getMessage());
        }

        @Test
        void RefreshToken을_통해서_AccessToken과_RefreshToken을_재발급받는다() {
            // given
            final Long memberId = 1L;
            final String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
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
