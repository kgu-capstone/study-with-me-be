package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import lombok.Builder;

public record StudyMember(
        Long id, String nickname
) {
    @Builder
    public StudyMember {}

    public StudyMember(Member host) {
        this(host.getId(), host.getNicknameValue());
    }
}
