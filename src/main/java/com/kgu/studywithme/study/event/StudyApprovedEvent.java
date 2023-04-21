package com.kgu.studywithme.study.event;

public record StudyApprovedEvent(
        String email,
        String nickname,
        String studyName
) {
}
