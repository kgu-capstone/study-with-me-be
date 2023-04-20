package com.kgu.studywithme.fixture;

import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentFixture {
    PDF_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid-hello.pdf"),
    TXT_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid-hello.txt"),
    HWP_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid-hello.hwp"),
    IMG_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid-hello.png"),
    ;

    private final String link;

    public Attachment toAttachment(Week week) {
        return Attachment.addAttachmentFile(week, link);
    }
}
