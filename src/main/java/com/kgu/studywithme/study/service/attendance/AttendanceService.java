package com.kgu.studywithme.study.service.attendance;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudyValidator studyValidator;

    @Transactional
    public void manualCheckAttendance(Long studyId, Long memberId, Long hostId, Integer week, String status) {
        validateHost(studyId, hostId);

        Attendance attendance = getParticipantAttendance(studyId, memberId, week);
        attendance.updateAttendanceStatus(status);
    }

    private Attendance getParticipantAttendance(Long studyId, Long memberId, Integer week) {
        return attendanceRepository.findByStudyIdAndParticipantIdAndWeek(studyId, memberId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND));
    }

    private void validateHost(Long studyId, Long memberId) {
        studyValidator.validateHost(studyId, memberId);
    }
}
