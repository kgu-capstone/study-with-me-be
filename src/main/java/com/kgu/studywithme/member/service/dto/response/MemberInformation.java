package com.kgu.studywithme.member.service.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Region;

import java.time.LocalDate;
import java.util.List;

public record MemberInformation(
        Long id, String name, String nickname, String email, LocalDate birth,
        String phone, String gender, Region region, int score, List<String> interests
) {
    public MemberInformation(Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getNicknameValue(),
                member.getEmailValue(),
                member.getBirth(),
                member.getPhone(),
                member.getGender().getValue(),
                member.getRegion(),
                member.getScore(),
                translateInterests(member)
        );
    }

    private static List<String> translateInterests(Member member) {
        return member.getInterests()
                .stream()
                .map(Category::getName)
                .toList();
    }
}
