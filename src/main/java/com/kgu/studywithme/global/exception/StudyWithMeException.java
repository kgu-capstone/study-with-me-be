package com.kgu.studywithme.global.exception;

import lombok.Getter;

@Getter
public class StudyWithMeException extends RuntimeException {
    private final ErrorCode code;

    public StudyWithMeException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static StudyWithMeException type(ErrorCode code) {
        return new StudyWithMeException(code);
    }
}
