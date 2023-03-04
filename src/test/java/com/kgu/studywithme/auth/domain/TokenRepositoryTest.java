package com.kgu.studywithme.auth.domain;

import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Repository Layer] -> TokenRepository 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TokenRepositoryTest extends RepositoryTest {
    @Autowired
    private TokenRepository tokenRepository;

    @Test
    void 사용자_ID를_통해서_보유하고_있는_RefreshToken을_조회한다() {
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
    void RTR정책에_의해서_사용자가_보유하고_있는_RefreshToken을_재발급한다() {
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
    void 사용자가_보유하고_있는_RefreshToken인지_확인한다() {
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
    void 사용자가_보유하고_있는_RefreshToken을_삭제한다() {
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
