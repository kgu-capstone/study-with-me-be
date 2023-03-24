package com.kgu.studywithme.auth.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import lombok.Builder;

public record MemberInfo(
        Long id,
        String nickname,
        String email,
        String profileUrl
) {
    @Builder
    public MemberInfo {}

    private MemberInfo(Member member) {
        this(member.getId(), member.getNicknameValue(), member.getEmailValue(), member.getProfileUrl());
    }

    public static MemberInfo from(Member member) {
        return new MemberInfo(member);
    }
}
