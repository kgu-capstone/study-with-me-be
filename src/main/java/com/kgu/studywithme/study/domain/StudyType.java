package com.kgu.studywithme.study.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyType {
    ONLINE("온라인", "online"),
    OFFLINE("오프라인", "offline")
    ;

    private final String description;
    private final String brief;
}
