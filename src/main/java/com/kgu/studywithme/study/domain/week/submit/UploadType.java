package com.kgu.studywithme.study.domain.week.submit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadType {
    LINK("링크 제출"),
    FILE("파일 제출"),
    ;

    private final String description;
}
