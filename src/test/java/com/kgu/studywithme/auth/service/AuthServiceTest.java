package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> AuthService 테스트")
class AuthServiceTest extends ServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Member member;

    @BeforeEach
    void setUp() {
        member = SEO_JI_WON.toMember();
        memberRepository.save(member);
    }

    @Nested
    @DisplayName("로그인을 진행할 때 ")
    class login {
        @Test
        @DisplayName("이메일에 해당하는 사용자가 존재하지 않으면 예외가 발생한다")
        void wrongEmail() {
            // given
            final String email = "diff" + SEO_JI_WON.getEmail();
            final String password = SEO_JI_WON.getPassword();
            
            // when - then
            assertThatThrownBy(() -> authService.login(email, password))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
        
        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
        void wrongPassword() {
            // given
            final String email = SEO_JI_WON.getEmail();
            final String password = "diff" + SEO_JI_WON.getPassword();
            
            // when - then
            assertThatThrownBy(() -> authService.login(email, password))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.WRONG_PASSWORD.getMessage());
        }
        
        @Test
        @DisplayName("정보가 모두 일치하면 로그인에 성공하고 Access Token과 Refresh Token을 발급받는다")
        void success() {
            // given
            final String email = SEO_JI_WON.getEmail();
            final String password = SEO_JI_WON.getPassword();

            // when
            TokenResponse response = authService.login(email, password);

            // then
            assertAll(
                    () -> assertThat(response).isNotNull(),
                    () -> assertThat(response)
                            .usingRecursiveComparison()
                            .isNotNull(),
                    () -> assertThat(jwtTokenProvider.getId(response.accessToken())).isEqualTo(member.getId()),
                    () -> assertThat(jwtTokenProvider.getId(response.refreshToken())).isEqualTo(member.getId()),
                    () -> {
                        Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
                        assertThat(findToken.getRefreshToken()).isEqualTo(response.refreshToken());
                    }
            );
        }
    }

    @Test
    @DisplayName("로그아웃을 진행하면 사용자에게 발급되었던 RefreshToken이 DB에서 삭제된다")
    void logout() {
        // given
        final Long memberId = member.getId();

        // when
        authService.logout(memberId);

        // then
        Optional<Token> findToken = tokenRepository.findByMemberId(memberId);
        assertThat(findToken).isEmpty();
    }
}
