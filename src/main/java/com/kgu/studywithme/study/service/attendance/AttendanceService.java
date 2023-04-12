package com.kgu.studywithme.study.service.attendance;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.StudyFindService;
import com.kgu.studywithme.study.service.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public void manualCheckAttendance(Long studyId, Long participantId, Long hostId, String status, Integer week) {
        validateHost(studyId, hostId);

        Study study = studyFindService.findById(studyId);
        Member participant = memberFindService.findById(participantId);
        study.validateMemberIsParticipant(participant);

        Attendance attendance = attendanceRepository.findByStudyAndParticipantAndWeek(study, participant, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND));
        attendance.updateAttendanceStatus(changeStringtoAttendanceStatus(status));
    }

    private void validateHost(Long studyId, Long memberId) {
        studyValidator.validateHost(studyId, memberId);
    }

    private AttendanceStatus changeStringtoAttendanceStatus(String status) {
        if (AttendanceStatus.NON_ATTENDANCE.getDescription().equals(status)) {
            return AttendanceStatus.NON_ATTENDANCE;
        } else if (AttendanceStatus.ATTENDANCE.getDescription().equals(status)) {
            return AttendanceStatus.ATTENDANCE;
        } else if (AttendanceStatus.LATE.getDescription().equals(status)) {
            return AttendanceStatus.LATE;
        } else {
            return AttendanceStatus.ABSENCE;
        }
    }
}
