package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.member.controller.dto.request.MemberReviewRequest;
import com.kgu.studywithme.member.service.MemberReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{revieweeId}/review")
public class MemberReviewApiController {
    private final MemberReviewService memberReviewService;

    @PostMapping
    public ResponseEntity<Void> writeReview(@PathVariable Long revieweeId,
                                            @ExtractPayload Long reviewerId,
                                            @RequestBody @Valid MemberReviewRequest request) {
        memberReviewService.writeReview(revieweeId, reviewerId, request.content());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateReview(@PathVariable Long revieweeId,
                                             @ExtractPayload Long reviewerId,
                                             @RequestBody @Valid MemberReviewRequest request) {
        memberReviewService.updateReview(revieweeId, reviewerId, request.content());
        return ResponseEntity.noContent().build();
    }
}