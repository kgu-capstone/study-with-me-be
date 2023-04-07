package com.kgu.studywithme.study.controller;

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
    public ResponseEntity<Void> apply(@PathVariable Long studyId, @ExtractPayload Long memberId) {
        participationService.apply(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/applicants")
    public ResponseEntity<Void> applyCancel(@PathVariable Long studyId, @ExtractPayload Long applierId) {
        participationService.applyCancel(studyId, applierId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/applicants/{applierId}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long studyId,
                                        @PathVariable Long applierId,
                                        @ExtractPayload Long hostId) {
        participationService.approve(studyId, applierId, hostId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/applicants/{applierId}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long studyId,
                                       @PathVariable Long applierId,
                                       @ExtractPayload Long hostId) {
        participationService.reject(studyId, applierId, hostId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/participants/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long studyId, @ExtractPayload Long participantId) {
        participationService.cancel(studyId, participantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/participants/{participantId}/delegation")
    public ResponseEntity<Void> delegateAuthority(@PathVariable Long studyId,
                                                  @PathVariable Long participantId,
                                                  @ExtractPayload Long hostId) {
        participationService.delegateAuthority(studyId, participantId, hostId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/graduate")
    public ResponseEntity<Void> graduate(@PathVariable Long studyId, @ExtractPayload Long participantId) {
        participationService.graduate(studyId, participantId);
        return ResponseEntity.noContent().build();
    }
}
