package com.kgu.studywithme.study.service.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import com.kgu.studywithme.study.domain.week.submit.Upload;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.StudyFindService;
import com.kgu.studywithme.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyWeeklyService {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final WeekRepository weekRepository;
    private final AttendanceRepository attendanceRepository;
    private final FileUploader uploader;

    @Transactional
    public void createWeek(Long studyId, Integer week, StudyWeeklyRequest request) {
        Study study = studyFindService.findById(studyId);
        List<String> attachments = uploader.uploadWeeklyAttachments(request.files());

        createWeekBasedOnAssignmentExistence(study, week, attachments, request);
        processAttendance(study, week);
    }

    private void createWeekBasedOnAssignmentExistence(Study study, Integer week, List<String> attachments, StudyWeeklyRequest request) {
        if (request.assignmentExists()) {
            study.createWeekWithAssignment(
                    request.title(),
                    request.content(),
                    week,
                    Period.of(request.startDate(), request.endDate()),
                    request.autoAttendance(),
                    attachments
            );
        } else {
            study.createWeek(
                    request.title(),
                    request.content(),
                    week,
                    Period.of(request.startDate(), request.endDate()),
                    attachments
            );
        }
    }

    private void processAttendance(Study study, Integer week) {
        study.getApproveParticipants()
                .forEach(participant -> study.recordAttendance(participant, week, NON_ATTENDANCE));
    }

    @Transactional
    public void submitAssignment(Long participantId, Long studyId, Integer week, String type, MultipartFile file, String link) {
        validateAssignmentSubmissionExists(file, link);

        Week specificWeek = getSpecificWeek(studyId, week);
        Member participant = memberFindService.findById(participantId);

        handleAssignmentSubmission(specificWeek, participant, type, file, link);
        processAttendanceBasedOnAutoAttendanceFlag(specificWeek, participant, studyId);
    }

    private void validateAssignmentSubmissionExists(MultipartFile file, String link) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_SUBMISSION);
        }
    }

    private Week getSpecificWeek(Long studyId, Integer week) {
        return weekRepository.findByStudyIdAndWeek(studyId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.WEEK_NOT_FOUND));
    }

    private void handleAssignmentSubmission(Week week, Member participant, String type, MultipartFile file, String link) {
        Upload upload = type.equals("file")
                ? Upload.withFile(uploader.uploadWeeklySubmit(file))
                : Upload.withLink(link);

        week.submitAssignment(participant, upload);
    }

    private void processAttendanceBasedOnAutoAttendanceFlag(Week week, Member participant, Long studyId) {
        if (week.isAutoAttendance()) {
            final LocalDateTime now = LocalDateTime.now();
            final Period period = week.getPeriod();

            if (period.isDateWithInRange(now)) {
                applyAttendanceStatusAndMemberScore(participant, week.getWeek(), studyId);
            } else {
                applyLateStatusAndMemberScore(participant, week.getWeek(), studyId);
            }
        }
    }

    private void applyAttendanceStatusAndMemberScore(Member participant, int week, Long studyId) {
        Attendance attendance = getParticipantAttendance(studyId, participant.getId(), week);
        attendance.updateAttendanceStatus(ATTENDANCE);
        participant.applyScoreByAttendanceStatus(ATTENDANCE);
    }

    private void applyLateStatusAndMemberScore(Member participant, int week, Long studyId) {
        Attendance attendance = getParticipantAttendance(studyId, participant.getId(), week);
        final AttendanceStatus previousStatus = attendance.getStatus();

        attendance.updateAttendanceStatus(LATE);
        if (previousStatus == ABSENCE) { // 스케줄러에 의한 결석 처리
            participant.applyScoreByAttendanceStatus(ABSENCE, LATE);
        } else { // 미출결 상태
            participant.applyScoreByAttendanceStatus(LATE);
        }
    }

    private Attendance getParticipantAttendance(Long studyId, Long memberId, Integer week) {
        return attendanceRepository.findByStudyIdAndParticipantIdAndWeek(studyId, memberId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND));
    }
}
