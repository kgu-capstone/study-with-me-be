package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.infra.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infra.oauth.OAuthProperties;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DisplayName("Auth [Service Layer] -> OAuthService 테스트")
class OAuthServiceTest extends ServiceTest {
    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private OAuthProperties properties;

    @MockBean
    private OAuthConnector oAuthConnector;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static String authorizationCode;

    @BeforeEach
    void setUp() {
        authorizationCode = UUID.randomUUID().toString().replaceAll("-", "").repeat(2);
    }

    @Test
    @DisplayName("Google OAuth 인증을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생시키고 추가적인 정보를 기입해서 회원가입을 진행해야 한다")
    void throwExceptionIfGoogleAuthUserNotInDB() {
        // given
        GoogleTokenResponse googleTokenResponse = GoogleTokenResponse.builder()
                .tokenType(BEARER_TOKEN)
                .idToken(ID_TOKEN)
                .accessToken(ACCESS_TOKEN)
                .scope(SCOPE)
                .expiresIn(3600)
                .build();
        GoogleUserResponse googleUserResponse = GoogleUserResponse.builder()
                .name(SEO_JI_WON.getName())
                .email(SEO_JI_WON.getEmail())
                .picture("picture.png")
                .build();

        given(oAuthConnector.getToken(authorizationCode, properties.getRedirectUrl())).willReturn(googleTokenResponse);
        given(oAuthConnector.getUserInfo(ACCESS_TOKEN)).willReturn(googleUserResponse);

        // when - then
        StudyWithMeOAuthException exception = assertThrows(StudyWithMeOAuthException.class, () -> oAuthService.login(authorizationCode, properties.getRedirectUrl()));
        assertThat(exception.getResponse())
                .usingRecursiveComparison()
                .isEqualTo(googleUserResponse);
    }

    @Test
    @DisplayName("Google OAuth 인증을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 토큰을 발급해준다")
    void success() {
        // given
        Member member = SEO_JI_WON.toMember();
        memberRepository.save(member);

        GoogleTokenResponse googleTokenResponse = GoogleTokenResponse.builder()
                .tokenType(BEARER_TOKEN)
                .idToken(ID_TOKEN)
                .accessToken(ACCESS_TOKEN)
                .scope(SCOPE)
                .expiresIn(3600)
                .build();
        GoogleUserResponse googleUserResponse = GoogleUserResponse.builder()
                .name(SEO_JI_WON.getName())
                .email(SEO_JI_WON.getEmail())
                .picture("picture.png")
                .build();

        given(oAuthConnector.getToken(authorizationCode, properties.getRedirectUrl())).willReturn(googleTokenResponse);
        given(oAuthConnector.getUserInfo(ACCESS_TOKEN)).willReturn(googleUserResponse);

        // when
        TokenResponse tokenResponse = oAuthService.login(authorizationCode, properties.getRedirectUrl());

        // then
        assertAll(
                () -> assertThat(tokenResponse).isNotNull(),
                () -> assertThat(tokenResponse)
                        .usingRecursiveComparison()
                        .isNotNull(),
                () -> assertThat(jwtTokenProvider.getId(tokenResponse.accessToken())).isEqualTo(member.getId()),
                () -> assertThat(jwtTokenProvider.getId(tokenResponse.refreshToken())).isEqualTo(member.getId()),
                () -> {
                    Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
                    assertThat(findToken.getRefreshToken()).isEqualTo(tokenResponse.refreshToken());
                }
        );
    }
}
