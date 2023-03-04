package com.kgu.studywithme.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiGlobalExceptionHandler {
    @ExceptionHandler(StudyWithMeException.class)
    public ResponseEntity<ErrorResponse> studyWithMeException(StudyWithMeException exception) {
        ErrorCode code = exception.getCode();
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }
}
