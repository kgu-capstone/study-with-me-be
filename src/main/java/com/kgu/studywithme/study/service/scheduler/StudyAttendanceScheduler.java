package com.kgu.studywithme.study.service.scheduler;

import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicAttendance;
import com.kgu.studywithme.study.infra.query.dto.response.BasicWeekly;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ABSENCE;

@Component
@RequiredArgsConstructor
public class StudyAttendanceScheduler {
    private final StudyRepository studyRepository;
    private final AttendanceRepository attendanceRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void testPrint() {
        List<BasicWeekly> weeks = studyRepository.findAutoAttendanceAndPeriodEndWeek();
        List<BasicAttendance> attendances = studyRepository.findBasicAttendanceInformation();

        weeks.forEach(week -> {
            Long studyId = week.studyId();
            int specificWeek = week.week();
            Set<Long> participantIds = extractNonAttendanceParticipantIds(attendances, studyId, specificWeek);

            if (hasCandidates(participantIds)) {
                attendanceRepository.updateParticipantStatus(studyId, specificWeek, participantIds, ABSENCE);
            }
        });
    }

    private Set<Long> extractNonAttendanceParticipantIds(List<BasicAttendance> attendances, Long studyId, int week) {
        return attendances.stream()
                .filter(attendance -> attendance.studyId().equals(studyId) && attendance.week() == week)
                .map(BasicAttendance::participantId)
                .collect(Collectors.toSet());
    }

    private boolean hasCandidates(Set<Long> participantIds) {
        return !CollectionUtils.isEmpty(participantIds);
    }
}
