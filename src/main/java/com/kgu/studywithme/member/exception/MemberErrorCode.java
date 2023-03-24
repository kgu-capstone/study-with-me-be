package com.kgu.studywithme.member.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    // Nickname
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER_001", "닉네임 형식에 맞지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_002", "이미 사용중인 닉네임입니다."),

    // Email
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER_003", "구글 이메일 형식에 맞지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_004", "이미 사용중인 이메일입니다."),

    // Region
    REGION_IS_BLANK(HttpStatus.BAD_REQUEST, "MEMBER_005", "거주지를 정확하게 입력해주세요."),

    // Phone
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "MEMBER_006", "이미 사용중인 전화번호입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_007", "사용자 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
