package com.kgu.studywithme.fixture;

import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentFixture {
    PDF_FILE("hello_world.pdf"),
    TXT_FILE("hello_world.txt"),
    HWP_FILE("hello_world.hwp"),
    IMG_FILE("hello_world.img"),
    ;

    private final String link;

    public Attachment toAttachment(Week week) {
        return Attachment.addAttachmentFile(week, link);
    }
}
