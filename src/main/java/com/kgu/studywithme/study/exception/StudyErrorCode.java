package com.kgu.studywithme.study.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ErrorCode {
    // Capacity
    CAPACITY_OUT_OF_RANGE(HttpStatus.CONFLICT, "STUDY_001", "스터디 인원은 2명 이상 10명 이하여야 합니다."),

    // StudyName
    NAME_IS_BLANK(HttpStatus.BAD_REQUEST, "STUDY_002", "스터디 이름은 공백을 허용하지 않습니다."),
    NAME_LENGTH_OUT_OF_RANGE(HttpStatus.CONFLICT, "STUDY_003", "스터디 이름은 최대 20자까지 가능합니다."),

    // Description
    DESCRIPTION_LENGTH_OUT_OF_RANGE(HttpStatus.CONFLICT, "STUDY_004", "스터디 설명은 최대 1000자까지 가능합니다."),
    DESCRIPTION_IS_BLANK(HttpStatus.BAD_REQUEST, "STUDY_014", "스터디 설명은 공백을 허용하지 않습니다."),

    // Study
    ALREADY_CLOSED(HttpStatus.CONFLICT, "STUDY_005", "이미 종료된 스터디입니다."),
    RECRUITMENT_IS_COMPLETE(HttpStatus.CONFLICT, "STUDY_006", "스터디 모집이 마감되었습니다."),
    MEMBER_IS_HOST(HttpStatus.CONFLICT, "STUDY_007", "해당 스터디의 팀장입니다."),
    MEMBER_IS_PARTICIPANT(HttpStatus.CONFLICT, "STUDY_008", "이미 해당 스터디에 참여 신청을 했거나 참여중입니다."),
    STUDY_CAPACITY_IS_FULL(HttpStatus.CONFLICT, "STUDY_009", "스터디 모집 정원이 꽉 찼습니다."),
    MEMBER_IS_NOT_APPLIER(HttpStatus.CONFLICT, "STUDY_010", "스터디에 참여 신청을 한 사용자가 아닙니다."),
    MEMBER_IS_NOT_PARTICIPANT(HttpStatus.CONFLICT, "STUDY_011", "스터디 참여자가 아닙니다."),
    PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE(HttpStatus.CONFLICT, "STUDY_012", "과제 제출 기간의 시작일은 종료일 이전이어야 합니다."),
    PERIOD_END_DATE_MUST_BE_SET_FROM_NOW_ON(HttpStatus.CONFLICT, "STUDY_012", "종료일은 과거일 수 없습니다."),
    ALREADY_ASSIGNMENT_EXISTS_PER_WEEK(HttpStatus.CONFLICT, "STUDY_013", "해당 주차에는 이미 과제가 등록되어 있습니다."),
    STUDY_NOT_FOUND(HttpStatus.CONFLICT, "STUDY_015", "스터디가 존재하지 않습니다."),
    STUDY_AREA_IS_BLANK(HttpStatus.CONFLICT, "STUDY_016", "오프라인으로 진행되는 스터디는 진행 지역이 필수입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
