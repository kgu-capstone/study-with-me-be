package com.kgu.studywithme.study.domain.assignment.submit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Assignment-Submit 도메인 {Upload VO} 테스트")
class UploadTest {
    @Test
    @DisplayName("Link를 통해서 Upload를 생성한다")
    void constructWithLink() {
        Upload upload = Upload.withLink("https://google.com");

        assertAll(
                () -> assertThat(upload.getLink()).isEqualTo("https://google.com"),
                () -> assertThat(upload.getFile()).isNull(),
                () -> assertThat(upload.getType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("File을 통해서 Upload를 생성한다")
    void constructWithFile() {
        final MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "abc".getBytes());
        UploadFile uploadFile = UploadFile.from(file);
        Upload upload = Upload.withFile(uploadFile);

        assertAll(
                () -> assertThat(upload.getLink()).isNull(),
                () -> assertThat(upload.getFile()).isEqualTo(uploadFile),
                () -> assertThat(upload.getType()).isEqualTo(FILE)
        );
    }
}
