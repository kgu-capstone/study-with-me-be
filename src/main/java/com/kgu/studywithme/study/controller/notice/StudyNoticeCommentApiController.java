package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.NoticeCommentRequest;
import com.kgu.studywithme.study.service.notice.NoticeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices/{noticeId}")
public class StudyNoticeCommentApiController {
    private final NoticeCommentService noticeCommentService;

    @PostMapping("/comment")
    public ResponseEntity<Void> register(@PathVariable Long noticeId,
                                         @ExtractPayload Long memberId,
                                         @RequestBody @Valid NoticeCommentRequest request) {
        noticeCommentService.register(noticeId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> remove(@PathVariable Long noticeId,
                                       @PathVariable Long commentId,
                                       @ExtractPayload Long memberId) {
        noticeCommentService.remove(commentId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(@PathVariable Long noticeId,
                                       @PathVariable Long commentId,
                                       @ExtractPayload Long memberId,
                                       @RequestBody @Valid NoticeCommentRequest request) {
        noticeCommentService.update(commentId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }
}
