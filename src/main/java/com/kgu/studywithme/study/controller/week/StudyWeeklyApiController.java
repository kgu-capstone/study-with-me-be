package com.kgu.studywithme.study.controller.week;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.annotation.aop.CheckStudyHost;
import com.kgu.studywithme.global.annotation.aop.CheckStudyParticipant;
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
@RequestMapping("/api/studies/{studyId}/weeks/{week}")
public class StudyWeeklyApiController {
    private final StudyWeeklyService studyWeeklyService;

    @CheckStudyHost
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @PathVariable Integer week,
                                           @ModelAttribute @Valid StudyWeeklyRequest request) {
        studyWeeklyService.createWeek(studyId, week, request);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyParticipant
    @PostMapping(value = "/assignment", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> submitAssignment(@ExtractPayload Long memberId,
                                                 @PathVariable Long studyId,
                                                 @PathVariable Integer week,
                                                 @ModelAttribute @Valid WeeklyAssignmentSubmitRequest request) {
        studyWeeklyService.submitAssignment(memberId, studyId, week, request.type(), request.file(), request.link());
        return ResponseEntity.noContent().build();
    }

    @CheckStudyParticipant
    @PostMapping(value = "/assignment/edit", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editSubmittedAssignment(@ExtractPayload Long memberId,
                                                        @PathVariable Long studyId,
                                                        @PathVariable Integer week,
                                                        @ModelAttribute @Valid WeeklyAssignmentSubmitRequest request) {
        studyWeeklyService.editSubmittedAssignment(memberId, week, request.type(), request.file(), request.link());
        return ResponseEntity.noContent().build();
    }
}
