package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.study.service.StudyInformationService;
import com.kgu.studywithme.study.service.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyInformationApiController {
    private final StudyInformationService studyInformationService;

    @GetMapping
    public ResponseEntity<StudyInformation> getInformation(@PathVariable Long studyId) {
        StudyInformation response = studyInformationService.getInformation(studyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ReviewAssembler> getReviews(@PathVariable Long studyId) {
        ReviewAssembler response = studyInformationService.getReviews(studyId);
        return ResponseEntity.ok(response);
    }

    @CheckStudyParticipant
    @GetMapping("/notices")
    public ResponseEntity<NoticeAssembler> getNotices(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        NoticeAssembler response = studyInformationService.getNotices(studyId);
        return ResponseEntity.ok(response);
    }

    @CheckStudyHost
    @GetMapping("/applicants")
    public ResponseEntity<StudyApplicant> getApplicants(@ExtractPayload Long hostId, @PathVariable Long studyId) {
        StudyApplicant response = studyInformationService.getApplicants(studyId);
        return ResponseEntity.ok(response);
    }

    @CheckStudyParticipant
    @GetMapping("/attendances")
    public ResponseEntity<AttendanceAssmbler> getAttendances(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        AttendanceAssmbler response = studyInformationService.getAttendances(studyId);
        return ResponseEntity.ok(response);
    }

    @CheckStudyParticipant
    @GetMapping("/weeks")
    public ResponseEntity<WeeklyAssembler> getWeeks(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        WeeklyAssembler response = studyInformationService.getWeeks(studyId);
        return ResponseEntity.ok(response);
    }
}
