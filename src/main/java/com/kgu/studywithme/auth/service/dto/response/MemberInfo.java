package com.kgu.studywithme.auth.service.dto.response;

import com.kgu.studywithme.member.domain.Member;

public record MemberInfo(
        Long id,
        String nickname,
        String email
) {
    private MemberInfo(Member member) {
        this(member.getId(), member.getNicknameValue(), member.getEmailValue());
    }

    public static MemberInfo from(Member member) {
        return new MemberInfo(member);
    }
}
