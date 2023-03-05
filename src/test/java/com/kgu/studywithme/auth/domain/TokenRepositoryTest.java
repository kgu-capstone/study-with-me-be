package com.kgu.studywithme.auth.domain;

import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Repository Layer] -> TokenRepository 테스트")
class TokenRepositoryTest extends RepositoryTest {
    @Autowired
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("사용자 ID(PK)를 통해서 보유하고 있는 RefreshToken을 조회한다")
    void findRefreshTokenWithMemberId() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        Optional<Token> findToken = tokenRepository.findByMemberId(memberId);

        // then
        assertThat(findToken).isPresent();
        assertAll(
                () -> {
                    Token token = findToken.get();
                    assertThat(token.getMemberId()).isEqualTo(memberId);
                    assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
                }
        );
    }

    @Test
    @DisplayName("RTR정책에 의해서 사용자가 보유하고 있는 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        final String newRefreshToken = refreshToken + "reissue";
        tokenRepository.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByMemberId(memberId).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void checkMemberHasSpecificRefreshToken() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        final String fakeRefreshToken = "fake";
        boolean actual1 = tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
        boolean actual2 = tokenRepository.existsByMemberIdAndRefreshToken(memberId, fakeRefreshToken);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "hello_world_refresh_token";
        tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken));

        // when
        tokenRepository.deleteByMemberId(memberId);

        // then
        Optional<Token> findToken = tokenRepository.findByMemberId(memberId);
        assertThat(findToken).isEmpty();
    }
}
