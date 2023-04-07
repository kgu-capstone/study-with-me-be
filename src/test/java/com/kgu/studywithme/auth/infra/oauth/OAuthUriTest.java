package com.kgu.studywithme.auth.infra.oauth;

import com.kgu.studywithme.common.InfraTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Auth [Infra Layer] -> OAuthUri 테스트")
class OAuthUriTest extends InfraTest {
    private OAuthUri oAuthUri;

    @Mock
    private OAuthProperties properties;

    @BeforeEach
    void setUp() {
        given(properties.getAuthUrl()).willReturn("https://accounts.google.com/o/oauth2/v2/auth");
        given(properties.getClientId()).willReturn("client_id");
        given(properties.getScope()).willReturn(Set.of("openid", "profile", "email"));
        given(properties.getRedirectUrl()).willReturn("http://localhost:8080/login/oauth2/code/google");

        oAuthUri = new GoogleOAuthUri(properties);
    }

    @Test
    @DisplayName("Google Authorization Server로부터 Access Token을 받는 과정에서 선행적으로 Authorization Code를 받기 위한 URI를 생성한다")
    void generateAuthorizationCodeUri() {
        // when
        String uri = oAuthUri.generate(properties.getRedirectUrl());

        // then
        MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(properties.getClientId()),
                () -> assertThat(queryParams.getFirst("scope")).isEqualTo(String.join(" ", properties.getScope())),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(properties.getRedirectUrl())
        );
    }
}
