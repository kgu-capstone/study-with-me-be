package com.kgu.studywithme.study.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ErrorCode {
    CAPACITY_OUT_OF_RANGE(HttpStatus.CONFLICT, "STUDY_001", "스터디 인원은 2명 이상 10명 이하여야 합니다."),
    NAME_IS_BLANK(HttpStatus.BAD_REQUEST, "STUDY_002", "스터디 이름은 공백을 허용하지 않습니다."),
    NAME_LENGTH_OUT_OF_RANGE(HttpStatus.CONFLICT, "STUDY_003", "스터디 이름은 최대 20자까지 가능합니다."),
    DESCRIPTION_LENGTH_OUT_OF_RANGE(HttpStatus.CONFLICT, "STUDY_004", "스터디 설명은 최대 1000자까지 가능합니다."),
    ALREADY_CLOSED(HttpStatus.CONFLICT, "STUDY_005", "이미 종료된 스터디입니다."),
    RECRUITMENT_IS_COMPLETE(HttpStatus.CONFLICT, "STUDY_006", "스터디 모집이 마감되었습니다."),
    MEMBER_IS_HOST(HttpStatus.CONFLICT, "STUDY_007", "스터디 팀장입니다."),
    MEMBER_IS_PARTICIPANT(HttpStatus.CONFLICT, "STUDY_008", "이미 해당 스터디에 참여 신청을 했거나 참여중입니다."),
    STUDY_CAPACITY_IS_FULL(HttpStatus.CONFLICT, "STUDY_009", "스터디 모집 정원이 꽉 찼습니다."),
    MEMBER_IS_NOT_APPLIER(HttpStatus.CONFLICT, "STUDY_010", "스터디에 참여 신청을 한 사용자가 아닙니다."),
    MEMBER_IS_NOT_PARTICIPANT(HttpStatus.CONFLICT, "STUDY_011", "스터디 참여자가 아닙니다."),
    PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE(HttpStatus.CONFLICT, "STUDY_012", "시작일은 종료일 이전이어야 합니다."),
    PERIOD_END_DATE_MUST_BE_SET_FROM_NOW_ON(HttpStatus.CONFLICT, "STUDY_013", "종료일은 과거일 수 없습니다."),
    ALREADY_WEEK_CREATED(HttpStatus.CONFLICT, "STUDY_014", "이미 해당 주차와 관련된 내용이 등록되었습니다."),
    DESCRIPTION_IS_BLANK(HttpStatus.BAD_REQUEST, "STUDY_015", "스터디 설명은 공백을 허용하지 않습니다."),
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_016", "스터디가 존재하지 않습니다."),
    STUDY_LOCATION_IS_BLANK(HttpStatus.BAD_REQUEST, "STUDY_017", "오프라인으로 진행되는 스터디는 장소가 필수입니다."),
    ALREADY_REVIEW_WRITTEN(HttpStatus.CONFLICT, "STUDY_018", "이미 리뷰를 작성했습니다."),
    MEMBER_IS_NOT_GRADUATED(HttpStatus.CONFLICT, "STUDY_019", "스터디 졸업생이 아닙니다."),
    DUPLICATE_NAME(HttpStatus.CONFLICT, "STUDY_020", "이미 사용중인 스터디 이름입니다."),
    MEMBER_IS_NOT_HOST(HttpStatus.CONFLICT, "STUDY_021", "스터디 팀장이 아닙니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_022", "공지사항이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_023", "작성한 댓글이 존재하지 않습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_024", "작성한 리뷰가 존재하지 않습니다."),
    CAPACITY_CANNOT_BE_LESS_THAN_PARTICIPANTS(HttpStatus.CONFLICT, "STUDY_025", "현재 참여인원보다 더 적게 최대인원을 수정할 수 없습니다."),
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_026", "출석 정보가 존재하지 않습니다."),
    WEEK_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_027", "주차 정보가 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
