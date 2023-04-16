package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.StudyWeeksDTO;

import java.util.List;

public interface AttendanceQueryRepository {
    List<StudyWeeksDTO> findStudyIdAndWeekByParticipantId(Long participantId);
}
