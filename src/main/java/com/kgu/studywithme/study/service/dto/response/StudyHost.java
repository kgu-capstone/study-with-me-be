package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import lombok.Builder;

public record StudyHost(
        Long id, String nickname, String profileUrl
) {
    @Builder
    public StudyHost {}

    public StudyHost(Member host) {
        this(host.getId(), host.getNicknameValue(), host.getProfileUrl());
    }
}
