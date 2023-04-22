package com.kgu.studywithme.study.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyType {
    ONLINE("온라인", "ON"),
    OFFLINE("오프라인", "OFF")
    ;

    private final String description;
    private final String brief;
}
