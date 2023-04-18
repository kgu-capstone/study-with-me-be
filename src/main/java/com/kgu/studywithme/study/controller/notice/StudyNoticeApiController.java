package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyNoticeApiController {
    private final NoticeService noticeService;

    @PostMapping("/notice")
    public ResponseEntity<Void> register(@ExtractPayload Long hostId,
                                         @PathVariable Long studyId,
                                         @RequestBody @Valid NoticeRequest request) {
        noticeService.register(studyId, hostId, request.title(), request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Void> remove(@ExtractPayload Long hostId,
                                       @PathVariable Long studyId,
                                       @PathVariable Long noticeId) {
        noticeService.remove(studyId, noticeId, hostId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/notices/{noticeId}")
    public ResponseEntity<Void> update(@ExtractPayload Long hostId,
                                       @PathVariable Long studyId,
                                       @PathVariable Long noticeId,
                                       @RequestBody @Valid NoticeRequest request) {
        noticeService.update(studyId, noticeId, hostId, request.title(), request.content());
        return ResponseEntity.noContent().build();
    }
}
