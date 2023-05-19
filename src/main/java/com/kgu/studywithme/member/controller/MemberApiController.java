package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckMemberIdentity;
import com.kgu.studywithme.member.controller.dto.request.MemberReportRequest;
import com.kgu.studywithme.member.controller.dto.request.MemberUpdateRequest;
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

    @CheckMemberIdentity
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<Void> update(@ExtractPayload Long payloadId,
                                       @PathVariable Long memberId,
                                       @RequestBody @Valid MemberUpdateRequest request) {
        memberService.update(memberId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/members/{reporteeId}/report")
    public ResponseEntity<Void> report(@ExtractPayload Long reporterId,
                                       @PathVariable Long reporteeId,
                                       @RequestBody @Valid MemberReportRequest request) {
        memberService.report(reporteeId, reporterId, request.reason());
        return ResponseEntity.noContent().build();
    }
}
