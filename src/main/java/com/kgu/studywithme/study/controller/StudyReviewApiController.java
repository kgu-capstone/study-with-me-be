package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.ReviewRequest;
import com.kgu.studywithme.study.service.StudyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyReviewApiController {
    private final StudyReviewService studyReviewService;

    @PostMapping("/review")
    public ResponseEntity<Void> write(@ExtractPayload Long memberId,
                                      @PathVariable Long studyId,
                                      @RequestBody @Valid ReviewRequest request) {
        studyReviewService.write(studyId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> remove(@ExtractPayload Long memberId,
                                       @PathVariable Long studyId,
                                       @PathVariable Long reviewId) {
        studyReviewService.remove(reviewId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> update(@ExtractPayload Long memberId,
                                       @PathVariable Long studyId,
                                       @PathVariable Long reviewId,
                                       @RequestBody @Valid ReviewRequest request) {
        studyReviewService.update(reviewId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }
}
