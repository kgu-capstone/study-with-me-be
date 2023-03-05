package com.kgu.studywithme.category.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_EXIST(HttpStatus.NOT_FOUND, "CATEGORY_001", "존재하지 않는 카테고리입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
