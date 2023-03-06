package com.kgu.studywithme.auth.controller.utils;

import com.kgu.studywithme.auth.controller.dto.request.LoginRequest;

public class LoginRequestUtils {
    public static LoginRequest createRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
