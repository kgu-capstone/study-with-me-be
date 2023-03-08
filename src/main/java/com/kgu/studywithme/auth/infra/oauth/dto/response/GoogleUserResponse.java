package com.kgu.studywithme.auth.infra.oauth.dto.response;

import lombok.Builder;

public record GoogleUserResponse(
        String name,
        String email,
        String picture
) implements OAuthUserResponse {
    @Builder
    public GoogleUserResponse {}
}
