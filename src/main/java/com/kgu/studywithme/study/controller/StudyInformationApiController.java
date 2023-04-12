package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.global.annotation.ValidStudyParticipant;
import com.kgu.studywithme.study.service.StudyInformationService;
import com.kgu.studywithme.study.service.dto.response.NoticeAssembler;
import com.kgu.studywithme.study.service.dto.response.ReviewAssembler;
import com.kgu.studywithme.study.service.dto.response.StudyApplicant;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
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

    @ValidStudyParticipant
    @GetMapping("/notices")
    public ResponseEntity<NoticeAssembler> getNotices(@PathVariable Long studyId, @ExtractPayload Long memberId) {
        NoticeAssembler response = studyInformationService.getNotices(studyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/applicants")
    public ResponseEntity<StudyApplicant> getApplicants(@PathVariable Long studyId) {
        StudyApplicant response = studyInformationService.getApplicants(studyId);
        return ResponseEntity.ok(response);
    }
}
