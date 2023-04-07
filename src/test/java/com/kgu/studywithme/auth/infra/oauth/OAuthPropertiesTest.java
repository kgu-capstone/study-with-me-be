package com.kgu.studywithme.auth.infra.oauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DisplayName("Auth [Infra Layer] -> OAuthProperties 테스트")
class OAuthPropertiesTest {
    @Autowired
    private OAuthProperties properties;

    @Test
    @DisplayName("Google OAuth Properties가 제대로 Binding되는지 확인한다")
    void check() {
        assertAll(
                () -> assertThat(properties.getGrantType()).isEqualTo("authorization_code"),
                () -> assertThat(properties.getClientId()).isEqualTo("client_id"),
                () -> assertThat(properties.getClientSecret()).isEqualTo("client_secret"),
                () -> assertThat(properties.getRedirectUrl()).isEqualTo("http://localhost:8080/login/oauth2/code/google"),
                () -> assertThat(properties.getScope()).containsAll(List.of("openid", "profile", "email")),
                () -> assertThat(properties.getAuthUrl()).isEqualTo("https://accounts.google.com/o/oauth2/v2/auth"),
                () -> assertThat(properties.getTokenUrl()).isEqualTo("https://www.googleapis.com/oauth2/v4/token"),
                () -> assertThat(properties.getUserInfoUrl()).isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo")
        );
    }
}
