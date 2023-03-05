package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> TokenManager 테스트")
class TokenManagerTest extends ServiceTest {
    @Autowired
    private TokenManager tokenManager;

    @Nested
    @DisplayName("RefreshToken 동기화를 할 때 ")
    class synchronizedRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하고 있지 않은 사용자에게는 새로운 RefreshToken을 발급한다")
        void reissueRefreshToken() {
            // given
            final Long memberId = 1L;
            final String refreshToken = "hello_world_refresh_token";

            // when
            tokenManager.synchronizeRefreshToken(memberId, refreshToken);

            // then
            Token findToken = tokenRepository.findByMemberId(memberId).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자에게는 새로운 RefreshToken으로 업데이트한다")
        void updateRefreshToken() {
            // given
            final Long memberId = 1L;
            final String refreshToken = "hello_world_refresh_token";
            tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

            // when
            final String newRefreshToken = refreshToken + "new";
            tokenManager.synchronizeRefreshToken(memberId, newRefreshToken);

            // then
            Token findToken = tokenRepository.findByMemberId(memberId).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
        }
    }

    @Test
    @DisplayName("RTR정책에 의해서 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        final String newRefreshToken = refreshToken + "new";
        tokenManager.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByMemberId(memberId).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        tokenManager.deleteRefreshTokenByMemberId(memberId);

        // then
        assertThat(tokenRepository.findByMemberId(memberId)).isEmpty();
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void checkMemberHasSpecificRefreshToken() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        final String fakeRefreshToken = refreshToken + "fake";
        boolean actual1 = tokenManager.isRefreshTokenExists(memberId, refreshToken);
        boolean actual2 = tokenManager.isRefreshTokenExists(memberId, fakeRefreshToken);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
