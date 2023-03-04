package com.kgu.studywithme.auth.service.dto.response;

import lombok.Builder;

public record TokenResponse(
        String accessToken, String refreshToken
) {
    @Builder
    public TokenResponse {
    }
}
