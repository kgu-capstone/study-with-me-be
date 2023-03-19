package com.kgu.studywithme.study.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentStatus {
    IN_PROGRESS("모집 중"),
    COMPLETE("모집 완료")
    ;

    private final String description;
}
