package com.kgu.studywithme.auth.infra.oauth;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.common.InfraTest;
import com.kgu.studywithme.common.utils.TokenUtils;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@DisplayName("Auth [Infra Layer] -> OAuthConnector 테스트")
class OAuthConnectorTest extends InfraTest {
    private OAuthConnector oAuthConnector;

    @Mock
    private OAuthProperties properties;

    @Mock
    private RestTemplate restTemplate;

    private static final String AUTHORIZATION_CODE = "authoriation_code";
    private static final String REDIRECT_URL = "http://localhost:8080/login/oauth2/code/google";
    private static final String ACCESS_TOKEN = TokenUtils.ACCESS_TOKEN;

    @BeforeEach
    void setUp() {
        oAuthConnector = new GoogleOAuthConnector(properties, restTemplate);
    }

    @Nested
    @DisplayName("Token 응답받기")
    class getToken {
        @Test
        @DisplayName("Google Server와의 통신 불량으로 인해 예외가 발생한다")
        void failure() {
            // given
            given(properties.getGrantType()).willReturn("authorization_code");
            given(properties.getClientId()).willReturn("client_id");
            given(properties.getClientSecret()).willReturn("client_secret");
            given(properties.getTokenUrl()).willReturn("https://www.googleapis.com/oauth2/v4/token");
            given(restTemplate.postForEntity(eq(properties.getTokenUrl()), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                    .willThrow(RestClientException.class);

            // when - then
            assertThatThrownBy(() -> oAuthConnector.getToken(AUTHORIZATION_CODE, REDIRECT_URL))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION.getMessage());
        }

        @Test
        @DisplayName("Authorization Code & RedirectUrl를 통해서 Google Authorization Server로부터 Token을 응답받는다")
        void success() {
            // given
            given(properties.getGrantType()).willReturn("authorization_code");
            given(properties.getClientId()).willReturn("client_id");
            given(properties.getClientSecret()).willReturn("client_secret");
            given(properties.getTokenUrl()).willReturn("https://www.googleapis.com/oauth2/v4/token");

            GoogleTokenResponse response = TokenUtils.createGoogleTokenResponse();
            ResponseEntity<GoogleTokenResponse> responseEntity = ResponseEntity.ok(response);
            given(restTemplate.postForEntity(eq(properties.getTokenUrl()), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                    .willReturn(responseEntity);

            // when
            GoogleTokenResponse result = (GoogleTokenResponse) oAuthConnector.getToken(AUTHORIZATION_CODE, REDIRECT_URL);

            // then
            assertAll(
                    () -> assertThat(result.tokenType()).isEqualTo(BEARER_TOKEN),
                    () -> assertThat(result.idToken()).isEqualTo(ID_TOKEN),
                    () -> assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(result.scope()).isEqualTo(SCOPE),
                    () -> assertThat(result.expiresIn()).isEqualTo(EXPIRES_IN)
            );
        }
    }

    @Nested
    @DisplayName("사용자 정보 응답받기")
    class getUserInfo {
        @Test
        @DisplayName("Google Server와의 통신 불량으로 인해 예외가 발생한다")
        void failure() {
            // given
            given(properties.getUserInfoUrl()).willReturn("https://www.googleapis.com/oauth2/v3/userinfo");
            given(restTemplate.exchange(eq(properties.getUserInfoUrl()), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserResponse.class)))
                    .willThrow(RestClientException.class);

            // when - then
            assertThatThrownBy(() -> oAuthConnector.getUserInfo(ACCESS_TOKEN))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION.getMessage());
        }

        @Test
        @DisplayName("Access Token을 통해서 Google Server에 저장된 사용자 정보를 응답받는다")
        void success() {
            // given
            given(properties.getUserInfoUrl()).willReturn("https://www.googleapis.com/oauth2/v3/userinfo");

            GoogleUserResponse response = JIWON.toGoogleUserResponse();
            ResponseEntity<GoogleUserResponse> responseEntity = ResponseEntity.ok(response);
            given(restTemplate.exchange(eq(properties.getUserInfoUrl()), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserResponse.class)))
                    .willReturn(responseEntity);

            // when
            GoogleUserResponse result = (GoogleUserResponse) oAuthConnector.getUserInfo(ACCESS_TOKEN);

            // then
            assertAll(
                    () -> assertThat(result.name()).isEqualTo(JIWON.getName()),
                    () -> assertThat(result.email()).isEqualTo(JIWON.getEmail()),
                    () -> assertThat(result.picture()).isEqualTo("google_profile_url")
            );
        }
    }
}
