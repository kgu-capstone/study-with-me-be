package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.common.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
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

    private static final Long MEMBER_ID = 1L;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = jwtTokenProvider.createRefreshToken(MEMBER_ID);
    }

    @Nested
    @DisplayName("RefreshToken 동기화")
    class synchronizedRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하고 있지 않은 사용자에게는 새로운 RefreshToken을 발급한다")
        void reissueRefreshToken() {
            // when
            tokenManager.synchronizeRefreshToken(MEMBER_ID, refreshToken);

            // then
            Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자에게는 새로운 RefreshToken으로 업데이트한다")
        void updateRefreshToken() {
            // given
            tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, refreshToken));

            // when
            final String newRefreshToken = refreshToken + "new";
            tokenManager.synchronizeRefreshToken(MEMBER_ID, newRefreshToken);

            // then
            Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
        }
    }

    @Test
    @DisplayName("RTR정책에 의해서 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // given
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, refreshToken));

        // when
        final String newRefreshToken = refreshToken + "new";
        tokenManager.reissueRefreshTokenByRtrPolicy(MEMBER_ID, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, refreshToken));

        // when
        tokenManager.deleteRefreshTokenByMemberId(MEMBER_ID);

        // then
        assertThat(tokenRepository.findByMemberId(MEMBER_ID)).isEmpty();
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void checkMemberHasSpecificRefreshToken() {
        // given
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, refreshToken));

        // when
        boolean actual1 = tokenManager.isRefreshTokenExists(MEMBER_ID, refreshToken);
        boolean actual2 = tokenManager.isRefreshTokenExists(MEMBER_ID, refreshToken + "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
