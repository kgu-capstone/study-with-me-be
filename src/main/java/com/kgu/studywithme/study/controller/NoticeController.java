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
@RequestMapping("/api/studies/{studyId}")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/notice")
    public ResponseEntity<Void> register(@PathVariable Long studyId, @RequestBody @Valid NoticeRequest request, @ExtractPayload Long hostId) {
        noticeService.register(studyId, request, hostId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<Void> remove(@PathVariable Long studyId, @PathVariable Long noticeId, @ExtractPayload Long hostId) {
        noticeService.remove(studyId, noticeId, hostId);
        return ResponseEntity.noContent().build();
    }
}