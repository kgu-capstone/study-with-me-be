package com.kgu.studywithme.auth.service.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
