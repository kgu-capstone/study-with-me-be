package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyStatus;
import lombok.Builder;

import java.util.ArrayList;

// main page에서 카드 형식일 때
public record StudyResponse(
        Long id, String name, int currentMembers, ArrayList<String> hashtags, StudyStatus status
) {

    @Builder
    public StudyResponse {}

    public StudyResponse(Study study) {
        this(study.getId(), study.getName(), study.getCurrentMembers(), study.getHashtags(), study.getStatus());
    }

}
