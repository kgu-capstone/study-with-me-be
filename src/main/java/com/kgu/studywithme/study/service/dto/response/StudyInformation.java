package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyLocation;

import java.util.List;

public record StudyInformation(
        Long id, String name, String description, String category, String thumbnail, String thumbnailBackground, String type,
        StudyLocation location, String recruitmentStatus, int currentMembers, int maxMembers, List<String> hashtags,
        int minimumAttendanceForGraduation, int remainingOpportunityToUpdateGraduationPolicy,
        List<ParticipantInformation> participants, StudyMember host
) {
    public StudyInformation(Study study) {
        this(
                study.getId(),
                study.getNameValue(),
                study.getDescriptionValue(),
                study.getCategory().getName(),
                study.getThumbnail().getImageName(),
                study.getThumbnail().getBackground(),
                study.getType().getDescription(),
                study.getLocation(),
                study.getRecruitmentStatus().getDescription(),
                study.getApproveParticipants().size(),
                study.getMaxMembers(),
                study.getHashtags(),
                study.getMinimumAttendanceForGraduation(),
                study.getRemainingOpportunityToUpdateGraduationPolicy(),
                assemblingParticipantsInformation(study.getApproveParticipants()),
                new StudyMember(study.getHost())
        );
    }

    private static List<ParticipantInformation> assemblingParticipantsInformation(List<Member> approveParticipants) {
        return approveParticipants.stream()
                .map(ParticipantInformation::new)
                .toList();
    }
}
