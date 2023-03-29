package com.kgu.studywithme.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiGlobalExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(StudyWithMeException.class)
    public ResponseEntity<ErrorResponse> studyWithMeException(StudyWithMeException exception) {
        ErrorCode code = exception.getCode();
        logging(code);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    @ExceptionHandler(StudyWithMeOAuthException.class)
    public ResponseEntity<OAuthUserResponse> studyWithMeOAuthException(StudyWithMeOAuthException exception) {
        OAuthUserResponse response = exception.getResponse();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnonymousException(Exception e) {
        ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        logging(code, e.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 파라미터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> unsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException e) {
        return convert(GlobalErrorCode.VALIDATION_ERROR);
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) throws JsonProcessingException {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) throws JsonProcessingException {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    private String extractErrorMessage(List<FieldError> fieldErrors) throws JsonProcessingException {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return objectMapper.writeValueAsString(errors);
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler({NoHandlerFoundException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> noHandlerFoundException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR);
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR);
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_MEDIA_TYPE_ERROR);
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException() {
        return convert(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code) {
        logging(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code, String message) {
        logging(code, message);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(code, message));
    }

    private void logging(ErrorCode code) {
        log.warn("{} | {} | {}", code.getStatus(), code.getErrorCode(), code.getMessage());
    }

    private void logging(ErrorCode code, String message) {
        log.warn("{} | {} | {}", code.getStatus(), code.getErrorCode(), message);
    }
}
