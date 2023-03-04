package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> TokenManager 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TokenManagerTest extends ServiceTest {
    @Autowired
    private TokenManager tokenManager;

    @Test
    void RefreshToken을_보유하지_않은_사용자에게_새로운_RefreshToken을_발급한다() {
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
    void RefreshToken을_보유하고_있는_사용자에_대해서는_RefreshToken을_업데이트한다() {
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

    @Test
    void RTR정책에_의해서_RefreshToken을_재발급한다() {
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
    void 사용자가_보유하고_있는_RefreshToken_을_삭제한다() {
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
    void 해당_RefreshToken을_사용자가_보유하고_있는지_확인한다() {
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
                () -> {
                    assertThat(actual1).isTrue();
                    assertThat(actual2).isFalse();
                }
        );
    }
}