package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.NoticeCommentRequest;

public class NoticeCommentRequestUtils {
    public static NoticeCommentRequest createNoticeCommentRequest() {
        return NoticeCommentRequest.builder()
                .content("확인했습니다!")
                .build();
    }
}