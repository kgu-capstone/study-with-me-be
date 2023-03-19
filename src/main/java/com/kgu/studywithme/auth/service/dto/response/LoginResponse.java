package com.kgu.studywithme.auth.service.dto.response;

import lombok.Builder;

public record LoginResponse (
        MemberInfo member,
        String accessToken,
        String refreshToken
) {
    @Builder
    public LoginResponse {}
}
