package com.kgu.studywithme.auth.infra.oauth;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthTokenResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthUserResponse;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

@Component
@RequiredArgsConstructor
public class GoogleOAuthConnector implements OAuthConnector {
    private final OAuthProperties properties;
    private final RestTemplate restTemplate;

    private static final String BEARER_TYPE = "Bearer";

    @Override
    public OAuthTokenResponse getToken(String code, String redirectUri) {
        HttpHeaders headers = createTokenRequestHeader();
        MultiValueMap<String, String> params = applyTokenRequestParams(code, redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return fetchGoogleToken(request).getBody();
    }

    private HttpHeaders createTokenRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> applyTokenRequestParams(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", properties.getGrantType());
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        return params;
    }

    private ResponseEntity<GoogleTokenResponse> fetchGoogleToken(HttpEntity<MultiValueMap<String, String>> request) {
        try {
            return restTemplate.postForEntity(properties.getTokenUrl(), request, GoogleTokenResponse.class);
        } catch (RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }

    @Override
    public OAuthUserResponse getUserInfo(String accessToken) {
        HttpHeaders headers = createUserInfoRequestHeader(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return fetchGoogleUserInfo(request).getBody();
    }

    private HttpHeaders createUserInfoRequestHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<GoogleUserResponse> fetchGoogleUserInfo(HttpEntity<Void> request) {
        try {
            return restTemplate.exchange(properties.getUserInfoUrl(), GET, request, GoogleUserResponse.class);
        } catch (RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }
}
