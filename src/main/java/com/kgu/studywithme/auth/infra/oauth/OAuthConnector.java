package com.kgu.studywithme.auth.infra.oauth;

import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthTokenResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthUserResponse;

public interface OAuthConnector {
    OAuthTokenResponse getToken(String code, String redirectUri);
    OAuthUserResponse getUserInfo(String accessToken);
}
