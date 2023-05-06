package com.kgu.studywithme.common.utils;

import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;

public class TokenUtils {
    public static final String BEARER_TOKEN = "Bearer";
    public static final String ID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzc5NDQ0MjR9.t61tw4gDEBuXBn_DnCwiPIDaI-KcN9Zkn3QJSEK7fag";
    public static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzc5NDQ0MjR9.t61tw4gDEBuXBn_DnCwiPIDaI-KcN9Zkn3QJSEK7fag";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzg1NDIwMjR9.doqGa5Hcq6chjER1y5brJEv81z0njcJqeYxJb159ZX4";
    public static final String SCOPE = "openid https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
    public static final int EXPIRES_IN = 3000;

    public static TokenResponse createTokenResponse() {
        return new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
    }

    public static GoogleTokenResponse createGoogleTokenResponse() {
        return new GoogleTokenResponse(
                BEARER_TOKEN,
                ID_TOKEN,
                ACCESS_TOKEN,
                SCOPE,
                EXPIRES_IN
        );
    }
}
