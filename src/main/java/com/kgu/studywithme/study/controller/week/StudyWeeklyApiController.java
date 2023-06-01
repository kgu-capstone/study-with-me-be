package com.kgu.studywithme.study.controller.week;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.controller.dto.request.WeeklyAssignmentSubmitRequest;
import com.kgu.studywithme.study.service.week.StudyWeeklyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyWeeklyApiController {
    private final StudyWeeklyService studyWeeklyService;

    @CheckStudyHost
    @PostMapping(value = "/week", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @ModelAttribute @Valid StudyWeeklyRequest request) {
        studyWeeklyService.createWeek(studyId, request);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyHost
    @PostMapping(value = "/weeks/{week}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @PathVariable Integer week,
                                           @ModelAttribute @Valid StudyWeeklyRequest request) {
        studyWeeklyService.updateWeek(studyId, week, request);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyHost
    @DeleteMapping("/weeks/{week}")
    public ResponseEntity<Void> deleteWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @PathVariable Integer week) {
        studyWeeklyService.deleteWeek(studyId, week);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyParticipant
    @PostMapping(value = "/weeks/{week}/assignment", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> submitAssignment(@ExtractPayload Long memberId,
                                                 @PathVariable Long studyId,
                                                 @PathVariable Integer week,
                                                 @ModelAttribute @Valid WeeklyAssignmentSubmitRequest request) {
        studyWeeklyService.submitAssignment(memberId, studyId, week, request.type(), request.file(), request.link());
        return ResponseEntity.noContent().build();
    }

    @CheckStudyParticipant
    @PostMapping(value = "/weeks/{week}/assignment/edit", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editSubmittedAssignment(@ExtractPayload Long memberId,
                                                        @PathVariable Long studyId,
                                                        @PathVariable Integer week,
                                                        @ModelAttribute @Valid WeeklyAssignmentSubmitRequest request) {
        studyWeeklyService.editSubmittedAssignment(memberId, studyId, week, request.type(), request.file(), request.link());
        return ResponseEntity.noContent().build();
    }
}
