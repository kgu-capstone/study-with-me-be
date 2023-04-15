package com.kgu.studywithme.study.service.dto.response;

import java.util.List;
import java.util.Map;

public record AttendanceAssmbler(Map<StudyMember, List<AttendanceSummary>> summaries) {
}
