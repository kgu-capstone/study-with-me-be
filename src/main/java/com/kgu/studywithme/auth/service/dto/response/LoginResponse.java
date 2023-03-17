package com.kgu.studywithme.auth.service.dto.response;

import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import lombok.Builder;

public record LoginResponse (
        GoogleUserResponse userInfo, String accessToken, String refreshToken
) {
    @Builder
    public LoginResponse {}
}
