package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;

public record StudyMember(
        Long id, String nickname
) {
    public StudyMember(Member member) {
        this(member.getId(), member.getNicknameValue());
    }
}
