package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.domain.participant.ParticipantStatus;

public record StudyAttendanceMember(
        Long id,
        String nickname,
        ParticipantStatus participantStatus
) {
}
