package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyStatus;
import lombok.Builder;

import java.util.List;
import java.util.Set;

public record StudyResponse(
        Long id, String name, int currentMembers, Set<String> hashtags, StudyStatus status
) {

    @Builder
    public StudyResponse {}

    public StudyResponse(Study study) {
        this(study.getId(), study.getName(), study.getCurrentMembers(), study.getHashtags(), study.getStatus());
    }

}
