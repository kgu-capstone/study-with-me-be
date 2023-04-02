package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/{studyId}/notice")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@PathVariable Long studyId, @RequestBody @Valid NoticeRequest request, @ExtractPayload Long memberId) {
        noticeService.register(studyId, request, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/{noticeId}")
    public ResponseEntity<Void> remove(@PathVariable Long studyId, @PathVariable Long noticeId, @ExtractPayload Long memberId) {
        noticeService.remove(studyId, noticeId, memberId);
        return ResponseEntity.noContent().build();
    }
}