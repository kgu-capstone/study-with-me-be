package com.kgu.studywithme.upload.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UploadErrorCode implements ErrorCode {
    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "UPLOAD_001", "파일이 전송되지 않았습니다."),
    S3_UPLOAD_FAILURE(HttpStatus.BAD_REQUEST, "UPLOAD_002", "파일 업로드에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
