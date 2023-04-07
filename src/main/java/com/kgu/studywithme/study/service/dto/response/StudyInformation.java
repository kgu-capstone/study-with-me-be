package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.utils.MemberAgeCalculator;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyArea;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public record StudyInformation(
        Long id, String name, String description, String category, String type, StudyArea area, String recruitmentStatus,
        int currentMembers, int maxMembers, double averageAge, List<String> hashtags, StudyMember host
) {
    public StudyInformation(Study study) {
        this(
                study.getId(), study.getNameValue(), study.getDescriptionValue(), study.getCategory().getName(),
                study.getType().getDescription(), study.getArea(), study.getRecruitmentStatus().getDescription(),
                calcCurrentMembers(study), study.getMaxMembers(), calcAverageAge(study),
                study.getHashtags(), new StudyMember(study.getHost())
        );
    }

    private static int calcCurrentMembers(Study study) {
        return study.getApproveParticipants().size();
    }

    private static double calcAverageAge(Study study) {
        List<Integer> birthList = study.getApproveParticipants()
                .stream()
                .map(Member::getBirth)
                .map(birth -> Period.between(birth, LocalDate.now()).getYears())
                .toList();

        return MemberAgeCalculator.getAverage(birthList);
    }
}
