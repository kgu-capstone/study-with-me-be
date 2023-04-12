package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.member.controller.dto.request.MemberReportRequest;
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

    @PostMapping("/members/{memberId}/report")
    public ResponseEntity<Void> report(@PathVariable Long memberId,
                                       @ExtractPayload Long reporterId,
                                       @RequestBody @Valid MemberReportRequest request) {
        memberService.report(memberId, reporterId, request.reason());
        return ResponseEntity.noContent().build();
    }
}
