package com.kgu.studywithme.study.infra.query.dto.response;

import java.time.LocalDateTime;

public record StudyReview(
        Long id,
        String content,
        LocalDateTime writtenDate,
        LocalDateTime lastModifiedDate
) {
}
