package com.kgu.studywithme.auth.infra.oauth.dto.response;

public record GoogleUserResponse(
        String name,
        String email,
        String picture
) implements OAuthUserResponse {
}
