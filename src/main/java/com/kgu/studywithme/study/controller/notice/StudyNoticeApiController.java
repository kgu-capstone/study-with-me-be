package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.NoticeRegisterRequest;
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
    public ResponseEntity<Void> register(@PathVariable Long studyId,
                                         @ExtractPayload Long hostId,
                                         @RequestBody @Valid NoticeRegisterRequest request) {
        noticeService.register(studyId, hostId, request.title(), request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Void> remove(@PathVariable Long studyId,
                                       @PathVariable Long noticeId,
                                       @ExtractPayload Long hostId) {
        noticeService.remove(studyId, noticeId, hostId);
        return ResponseEntity.noContent().build();
    }
}
