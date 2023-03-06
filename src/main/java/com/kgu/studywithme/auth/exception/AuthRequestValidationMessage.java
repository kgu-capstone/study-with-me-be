package com.kgu.studywithme.auth.exception;

public class AuthRequestValidationMessage {
    public static class Login {
        public static final String EMAIL = "이메일은 필수입니다.";
        public static final String PASSWORD = "비밀번호는 필수입니다.";
    }
}
