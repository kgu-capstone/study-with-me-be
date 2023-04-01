package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.utils.MemberAgeCalculator;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyArea;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record StudyInformation(
        Long id, String name, String description, String category, String type, StudyArea area, String recruitmentStatus,
        int currentMembers, int maxMembers, double averageAge, List<String> hashtags, StudyHost host
) {
    @Builder
    public StudyInformation {
    }

    public StudyInformation(Study study) {
        this(
                study.getId(), study.getNameValue(), study.getDescriptionValue(), study.getCategory().getName(),
                study.getType().getDescription(), study.getArea(), study.getRecruitmentStatus().getDescription(),
                calcCurrentMembers(study), study.getMaxMembers(), calcAverageAge(study),
                study.getHashtags(), new StudyHost(study.getHost())
        );
    }

    private static int calcCurrentMembers(Study study) {
        return study.getApproveParticipants().size();
    }

    private static double calcAverageAge(Study study) {
        List<LocalDate> birthList = study.getApproveParticipants()
                .stream()
                .map(Member::getBirth)
                .toList();

        return MemberAgeCalculator.getAverage(birthList);
    }
}
