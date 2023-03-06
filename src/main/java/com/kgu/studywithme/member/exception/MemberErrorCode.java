package com.kgu.studywithme.member.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_001", "이메일 형식에 맞지 않습니다."),
    INVALID_PASSWORD_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_002", "비밀번호는 영문, 숫자, 특수문자를 각각 하나 이상 포함해야 하고 8자 이상 25자 이하여야 합니다."),
    PASSWORD_SAME_AS_BEFORE(HttpStatus.CONFLICT, "MEMBER_003", "이전과 동일한 비밀번호로 변경할 수 없습니다."),
    INVALID_REGION(HttpStatus.BAD_REQUEST, "MEMBER_004", "거주지를 정확하게 입력해주세요."),
    INVALID_NICKNAME_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_005", "닉네임 형식에 맞지 않습니다."),
    NICKNAME_SAME_AS_BEFORE(HttpStatus.BAD_REQUEST, "MEMBER_006", "이전과 동일한 닉네임으로 변경할 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_007", "이미 사용중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_008", "이미 사용중인 닉네임입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "MEMBER_009", "이미 사용중인 전화번호입니다"),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
