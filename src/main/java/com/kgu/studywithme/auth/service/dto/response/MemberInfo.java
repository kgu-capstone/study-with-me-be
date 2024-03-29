package com.kgu.studywithme.auth.service.dto.response;

import com.kgu.studywithme.member.domain.Member;

public record MemberInfo(
        Long id,
        String nickname,
        String email
) {
    public MemberInfo(Member member) {
        this(member.getId(), member.getNicknameValue(), member.getEmailValue());
    }
}
