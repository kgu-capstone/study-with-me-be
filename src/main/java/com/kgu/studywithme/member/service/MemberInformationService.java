package com.kgu.studywithme.member.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationService {
    private final MemberFindService memberFindService;

    public MemberInformation getInformation(Long memberId) {
        Member member = memberFindService.findByIdWithInterests(memberId);
        return new MemberInformation(member);
    }
}
