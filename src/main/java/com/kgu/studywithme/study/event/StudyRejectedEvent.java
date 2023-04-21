package com.kgu.studywithme.study.event;

public record StudyRejectedEvent(
        String email,
        String nickname,
        String studyName,
        String reason
) {
}
