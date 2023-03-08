package com.kgu.studywithme.auth.exception;

public class AuthRequestValidationMessage {
    public static class Login {
        public static final String EMAIL = "이메일은 필수입니다.";
        public static final String PASSWORD = "비밀번호는 필수입니다.";
    }

    public static class OAuthLogin {
        public static final String CODE = "Authorization Code는 필수입니다.";
        public static final String REDIRECT_URL = "Authorization Code 요청 시 포함되었던 redirectUrl과 반드시 일치해야 합니다.";
    }
}
