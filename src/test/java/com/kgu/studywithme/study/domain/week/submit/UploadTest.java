package com.kgu.studywithme.study.domain.week.submit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Submit 도메인 {Upload VO} 테스트")
class UploadTest {
    @Test
    @DisplayName("Notion, Blog 등 링크를 통해서 과제물을 제출한다")
    void constructWithLink() {
        Upload upload = Upload.withLink("https://notion.com");

        assertAll(
                () -> assertThat(upload.getLink()).isEqualTo("https://notion.com"),
                () -> assertThat(upload.getType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("파일 업로드를 통해서 과제물을 제출한다")
    void constructWithFile() {
        Upload upload = Upload.withFile("file_upload_link");

        assertAll(
                () -> assertThat(upload.getLink()).isEqualTo("file_upload_link"),
                () -> assertThat(upload.getType()).isEqualTo(FILE)
        );
    }
}
