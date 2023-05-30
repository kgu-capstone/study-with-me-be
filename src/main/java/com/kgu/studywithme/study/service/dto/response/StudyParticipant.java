package com.kgu.studywithme.study.service.dto.response;

import java.util.List;

public record StudyParticipant(StudyMember host, List<StudyMember> participants) {
}
