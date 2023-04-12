package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.member.controller.dto.request.MemberReportRequest;
import com.kgu.studywithme.member.service.MemberReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{reporteeId}/report")
public class MemberReportApiController {
    private final MemberReportService memberReportService;

    @PostMapping
    public ResponseEntity<Void> report(@PathVariable Long reporteeId,
                                       @ExtractPayload Long reporterId,
                                       @RequestBody @Valid MemberReportRequest request) {
        memberReportService.report(reporteeId, reporterId, request.reason());
        return ResponseEntity.noContent().build();
    }
}
