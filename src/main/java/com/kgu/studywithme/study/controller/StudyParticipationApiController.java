package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.CheckStudyHost;
import com.kgu.studywithme.global.annotation.CheckStudyParticipant;
import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyParticipationApiController {
    private final ParticipationService participationService;

    @PostMapping("/applicants")
    public ResponseEntity<Void> apply(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        participationService.apply(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/applicants")
    public ResponseEntity<Void> applyCancel(@ExtractPayload Long applierId, @PathVariable Long studyId) {
        participationService.applyCancel(studyId, applierId);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyHost
    @PatchMapping("/applicants/{applierId}/approve")
    public ResponseEntity<Void> approve(@ExtractPayload Long hostId,
                                        @PathVariable Long studyId,
                                        @PathVariable Long applierId) {
        participationService.approve(studyId, applierId, hostId);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyHost
    @PatchMapping("/applicants/{applierId}/reject")
    public ResponseEntity<Void> reject(@ExtractPayload Long hostId,
                                       @PathVariable Long studyId,
                                       @PathVariable Long applierId) {
        participationService.reject(studyId, applierId, hostId);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyParticipant
    @PatchMapping("/participants/cancel")
    public ResponseEntity<Void> cancel(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        participationService.cancel(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyHost
    @PatchMapping("/participants/{participantId}/delegation")
    public ResponseEntity<Void> delegateAuthority(@ExtractPayload Long hostId,
                                                  @PathVariable Long studyId,
                                                  @PathVariable Long participantId) {
        participationService.delegateAuthority(studyId, participantId, hostId);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyParticipant
    @PatchMapping("/graduate")
    public ResponseEntity<Void> graduate(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        participationService.graduate(studyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
