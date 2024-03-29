package com.kgu.studywithme.member.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_001", "닉네임 형식에 맞지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_002", "이미 사용중인 닉네임입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_003", "구글 이메일 형식에 맞지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_004", "이미 사용중인 이메일입니다."),
    REGION_IS_BLANK(HttpStatus.BAD_REQUEST, "MEMBER_005", "거주지를 정확하게 입력해주세요."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "MEMBER_006", "이미 사용중인 전화번호입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_007", "사용자 정보를 찾을 수 없습니다."),
    MEMBER_IS_NOT_WRITER(HttpStatus.CONFLICT, "MEMBER_008", "작성자가 아닙니다."),
    ALREADY_REVIEW(HttpStatus.CONFLICT, "MEMBER_009", "이미 리뷰한 사용자입니다."),
    PREVIOUS_REPORT_IS_STILL_PENDING(HttpStatus.CONFLICT, "MEMBER_010", "해당 사용자에 대해서 이전에 신고하신 내역이 처리중입니다."),
    SELF_REVIEW_NOT_ALLOWED(HttpStatus.CONFLICT, "MEMBER_011", "본인에게 피어리뷰를 남길 수 없습니다."),
    COMMON_STUDY_RECORD_NOT_FOUND(HttpStatus.CONFLICT, "MEMBER_012", "함께 스터디를 진행한 기록이 없습니다."),
    PEER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_013", "작성한 피어리뷰를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
