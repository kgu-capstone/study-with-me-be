package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;

import java.time.LocalDate;
import java.time.Period;

public record ParticipantInformation(
        Long id,
        String nickname,
        String gender,
        int score,
        int age
) {
    public ParticipantInformation(Member member) {
        this(
                member.getId(),
                member.getNicknameValue(),
                member.getGender().getValue(),
                member.getScore(),
                getMemberAge(member.getBirth())
        );
    }

    private static int getMemberAge(LocalDate birth) {
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
