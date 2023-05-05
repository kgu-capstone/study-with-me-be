package com.kgu.studywithme.auth.service.dto.response;

public record LoginResponse (
        MemberInfo member,
        String accessToken,
        String refreshToken
) {
}
