package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import lombok.Builder;

public record StudyMember(
        Long id, String nickname
) {
    @Builder
    public StudyMember {}

    public StudyMember(Member member) {
        this(member.getId(), member.getNicknameValue());
    }
}
