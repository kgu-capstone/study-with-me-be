package com.kgu.studywithme.auth.infra.oauth.dto.response;

import lombok.Builder;

public record GoogleTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        Long expiresIn
) implements OAuthTokenResponse {
    @Builder
    public GoogleTokenResponse {}
}
