package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;

public class NoticeRequestUtils {
    public static NoticeRequest createNoticeRequest() {
        return NoticeRequest.builder()
                .title("4/3 공지사항입니다.")
                .content("4/7 사당역 스터디룸에서 5시까지 모여주세요.")
                .build();
    }
}