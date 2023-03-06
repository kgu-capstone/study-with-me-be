package com.kgu.studywithme.member.service;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberSignupService {
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;

    @Transactional
    public Long signUp(Member member, List<Long> categories) {
        validateUniqueFields(member);
        applyInterestCategories(member, categories);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(Member member) {
        memberValidator.validateEmail(member.getEmail());
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validatePhone(member.getPhone());
    }

    private void applyInterestCategories(Member member, List<Long> categories) {
        member.addCategoriesToInterests(
                categories.stream()
                        .map(Category::from)
                        .collect(Collectors.toSet())
        );
    }
}
