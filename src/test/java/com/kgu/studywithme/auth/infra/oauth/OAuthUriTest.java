package com.kgu.studywithme.auth.infra.oauth;

import com.kgu.studywithme.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Infra Layer] -> OAuthUri 테스트")
class OAuthUriTest extends ServiceTest {
    @Autowired
    private OAuthProperties properties;

    @Autowired
    private OAuthUri oAuthUri;

    @Test
    @DisplayName("Google Authorization Server로부터 Access Token을 받는 과정에서 선행적으로 Authorization Code를 받기 위한 URI를 생성한다")
    void generateAuthorizationCodeUri() {
        String uri = oAuthUri.generate(properties.getRedirectUrl());

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
