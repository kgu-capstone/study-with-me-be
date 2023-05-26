package com.kgu.studywithme.fixture;

import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentFixture {
    PDF_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid.pdf"),
    TXT_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid.txt"),
    HWP_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid.hwp"),
    IMG_FILE("https://kr.object.ncloudstorage.com/bucket/attachments/uuid.png"),
    ;

    private final String link;

    public Attachment toAttachment(Week week) {
        return Attachment.addAttachmentFile(week, link);
    }
}
