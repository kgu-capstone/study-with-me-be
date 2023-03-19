package com.kgu.studywithme.auth.controller.utils;

import com.kgu.studywithme.auth.controller.dto.request.OAuthLoginRequest;

public class OAuthLoginRequestUtils {
    public static OAuthLoginRequest createOAuthLoginRequest(String code, String redirectUrl) {
        return OAuthLoginRequest.builder()
                .code(code)
                .redirectUrl(redirectUrl)
                .build();
    }
}
