package com.kgu.studywithme.study.service.week;

import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.service.StudyFindService;
import com.kgu.studywithme.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyWeeklyService {
    private final StudyFindService studyFindService;
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
}
