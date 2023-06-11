package com.kgu.studywithme.study.service.dto.response;

import java.util.List;

public record StudyMemberAttendanceResult(
        StudyMember member,
        List<AttendanceSummary> summaries
) {
}
