package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Auth [Utils] -> JwtTokenProvider 테스트")
class JwtTokenProviderTest {
    private static final String SECRET_KEY = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01";
    private static final JwtTokenProvider INVALID_PROVIDER = new JwtTokenProvider(SECRET_KEY, 0L, 0L);
    private static final JwtTokenProvider VALID_PROVIDER = new JwtTokenProvider(SECRET_KEY, 7200L, 7200L);

    @Test
    @DisplayName("AccessToken과 RefreshToken을 발급한다")
    void issueAccessTokenAndRefreshToken() {
        // given
        final Long memberId = 1L;
        
        // when
        String accessToken = VALID_PROVIDER.createAccessToken(memberId);
        String refreshToken = VALID_PROVIDER.createRefreshToken(memberId);

        // then
        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull()
        );
    }

    @Test
    @DisplayName("Token의 Payload를 추출한다")
    void extractTokenPayload() {
        // given
        final Long memberId = 1L;

        // when
        String accessToken = VALID_PROVIDER.createAccessToken(memberId);

        // then
        assertThat(VALID_PROVIDER.getId(accessToken)).isEqualTo(memberId);
    }

    @Test
    @DisplayName("Token 만료에 대한 유효성을 검증한다")
    void validateTokenExpire() {
        // given
        final Long memberId = 1L;

        // when
        String validToken = VALID_PROVIDER.createAccessToken(memberId);
        String invalidToken = INVALID_PROVIDER.createAccessToken(memberId);

        // then
        assertDoesNotThrow(() -> VALID_PROVIDER.isTokenValid(validToken));
        assertThatThrownBy(() -> INVALID_PROVIDER.isTokenValid(invalidToken))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.AUTH_EXPIRED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("Token 조작에 대한 유효성을 검증한다")
    void validateTokenManipulation() {
        // given
        final Long memberId = 1L;

        // when
        String forgedToken = VALID_PROVIDER.createAccessToken(memberId) + "hacked";

        // then
        assertThatThrownBy(() -> VALID_PROVIDER.isTokenValid(forgedToken))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.AUTH_INVALID_TOKEN.getMessage());
    }
}
