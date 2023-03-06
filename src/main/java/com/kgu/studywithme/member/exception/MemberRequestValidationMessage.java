package com.kgu.studywithme.member.exception;

public class MemberRequestValidationMessage {
    public static class SignUp {
        public static final String MEMBER_NAME = "이름은 필수입니다.";
        public static final String MEMBER_NICKNAME = "닉네임은 필수입니다.";
        public static final String MEMBER_EMAIL = "이메일은 필수입니다.";
        public static final String MEMBER_PASSWORD = "비밀번호는 필수입니다.";
        public static final String MEMBER_BIRTH = "생년월일은 필수입니다.";
        public static final String MEMBER_PHONE = "전화번호는 필수입니다.";
        public static final String MEMBER_GENDER = "성별은 필수입니다.";
        public static final String MEMBER_REGION = "거주지는 필수입니다.";
        public static final String MEMBER_INTERESTS = "관심사는 하나 이상 등록해야 합니다.";
    }
}
