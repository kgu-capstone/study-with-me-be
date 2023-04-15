package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.member.controller.dto.request.MemberReportRequest;
import com.kgu.studywithme.member.controller.dto.request.MemberReviewRequest;
import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import com.kgu.studywithme.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        Long savedMemberId = memberService.signUp(request.toEntity());
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/members/{id}").build(savedMemberId))
                .build();
    }

    @PostMapping("/members/{reporteeId}/report")
    public ResponseEntity<Void> report(@PathVariable Long reporteeId,
                                       @ExtractPayload Long reporterId,
                                       @RequestBody @Valid MemberReportRequest request) {
        memberService.report(reporteeId, reporterId, request.reason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/members/{revieweeId}/review")
    public ResponseEntity<Void> writeReview(@PathVariable Long revieweeId,
                                            @ExtractPayload Long reviewerId,
                                            @RequestBody @Valid MemberReviewRequest request) {
        memberService.writeReview(revieweeId, reviewerId, request.content());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{revieweeId}/review")
    public ResponseEntity<Void> updateReview(@PathVariable Long revieweeId,
                                             @ExtractPayload Long reviewerId,
                                             @RequestBody @Valid MemberReviewRequest request) {
        memberService.updateReview(revieweeId, reviewerId, request.content());
        return ResponseEntity.noContent().build();
    }
}
