package com.kgu.studywithme.study.domain.week.attachment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Attachment 도메인 {UploadAttachment VO} 테스트")
class UploadAttachmentTest {
    @Test
    @DisplayName("주차에 대한 첨부파일을 업로드한다")
    void constructWithLink() {
        UploadAttachment uploadAttachment = UploadAttachment.of("hello.pdf", "uuid.pdf");

        assertAll(
                () -> assertThat(uploadAttachment.getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(uploadAttachment.getLink()).isEqualTo("uuid.pdf")
        );
    }
}
