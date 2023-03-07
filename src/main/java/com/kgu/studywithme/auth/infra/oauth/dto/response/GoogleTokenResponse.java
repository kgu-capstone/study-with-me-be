package com.kgu.studywithme.auth.infra.oauth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record GoogleTokenResponse(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("scope") String scope,
        @JsonProperty("expires_in") Integer expiresIn
) implements OAuthTokenResponse {
    @Builder
    public GoogleTokenResponse {}
}
