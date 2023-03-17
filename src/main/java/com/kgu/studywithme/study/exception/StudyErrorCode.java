package com.kgu.studywithme.study.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ErrorCode {
    // Capacity
    CAPACITY_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "STUDY_001", "스터디 인원은 2명 이상 10명 이하여야 합니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
