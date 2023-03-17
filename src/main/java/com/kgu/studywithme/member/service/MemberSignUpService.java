package com.kgu.studywithme.member.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberSignUpService {
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;

    @Transactional
    public Long signUp(Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(Member member) {
        memberValidator.validateEmail(member.getEmail());
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validatePhone(member.getPhone());
    }
}
