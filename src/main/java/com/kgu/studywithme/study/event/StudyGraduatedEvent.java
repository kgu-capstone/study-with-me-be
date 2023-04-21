package com.kgu.studywithme.study.event;

public record StudyGraduatedEvent(
        String email,
        String nickname,
        String studyName
) {
}
