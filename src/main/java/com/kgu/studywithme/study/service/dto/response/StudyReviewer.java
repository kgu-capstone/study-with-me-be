package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import lombok.Builder;

public record StudyReviewer(
        Long id, String nickname, String profileUrl
) {
    @Builder
    public StudyReviewer {}

    public StudyReviewer(Member member) {
        this(member.getId(), member.getNicknameValue(), member.getProfileUrl());
    }
}
