package com.kgu.studywithme.auth.domain;

import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Repository Layer] -> TokenRepository 테스트")
class TokenRepositoryTest extends RepositoryTest {
    @Autowired
    private TokenRepository tokenRepository;

    private static final Long MEMBER_ID = 1L;

    @BeforeEach
    void setUp() {
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, REFRESH_TOKEN));
    }

    @Test
    @DisplayName("사용자 ID(PK)를 통해서 보유하고 있는 RefreshToken을 조회한다")
    void findRefreshTokenWithMemberId() {
        // when
        Optional<Token> findToken = tokenRepository.findByMemberId(MEMBER_ID);

        // then
        assertThat(findToken).isPresent();
        assertAll(
                () -> {
                    Token token = findToken.get();
                    assertThat(token.getMemberId()).isEqualTo(MEMBER_ID);
                    assertThat(token.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
                }
        );
    }

    @Test
    @DisplayName("RTR정책에 의해서 사용자가 보유하고 있는 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // when
        final String newRefreshToken = REFRESH_TOKEN + "reissue";
        tokenRepository.reissueRefreshTokenByRtrPolicy(MEMBER_ID, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void checkMemberHasSpecificRefreshToken() {
        // when
        final String fakeRefreshToken = "fake";
        boolean actual1 = tokenRepository.existsByMemberIdAndRefreshToken(MEMBER_ID, REFRESH_TOKEN);
        boolean actual2 = tokenRepository.existsByMemberIdAndRefreshToken(MEMBER_ID, fakeRefreshToken);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // when
        tokenRepository.deleteByMemberId(MEMBER_ID);

        // then
        Optional<Token> findToken = tokenRepository.findByMemberId(MEMBER_ID);
        assertThat(findToken).isEmpty();
    }
}
