package com.kgu.studywithme.fixture;

import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentFixture {
    PDF_FILE("hello.pdf", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.pdf"),
    TXT_FILE("hello.txt", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.txt"),
    HWP_FILE("hello.hwp", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.hwp"),
    IMG_FILE("hello.img", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.png"),
    ;

    private final String uploadFileName;
    private final String link;

    public Attachment toAttachment(Week week) {
        return Attachment.addAttachmentFile(week, UploadAttachment.of(uploadFileName, link));
    }
}
