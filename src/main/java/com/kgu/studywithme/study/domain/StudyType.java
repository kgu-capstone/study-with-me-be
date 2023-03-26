package com.kgu.studywithme.study.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyType {
    ONLINE("온라인"),
    OFFLINE("오프라인")
    ;

    private final String description;
}
