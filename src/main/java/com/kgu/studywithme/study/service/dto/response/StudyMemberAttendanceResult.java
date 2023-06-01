package com.kgu.studywithme.study.service.dto.response;

import java.util.List;

public record StudyMemberAttendanceResult(
        StudyAttendanceMember member,
        List<AttendanceSummary> summaries
) {
}
