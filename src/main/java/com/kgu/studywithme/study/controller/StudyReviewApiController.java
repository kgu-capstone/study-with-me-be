package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
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
    public ResponseEntity<Void> write(@PathVariable Long studyId,
                                      @ExtractPayload Long memberId,
                                      @RequestBody @Valid ReviewRequest request) {
        studyReviewService.write(studyId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> remove(@PathVariable Long studyId,
                                       @PathVariable Long reviewId,
                                       @ExtractPayload Long memberId) {
        studyReviewService.remove(reviewId, memberId);
        return ResponseEntity.noContent().build();
    }
}
