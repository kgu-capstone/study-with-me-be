package com.kgu.studywithme.study.domain.assignment.submit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Assignment-Submit 도메인 {UploadFile VO} 테스트")
class UploadFileTest {
    @Test
    @DisplayName("UploadFile을 생성한다")
    void construct() {
        final MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "abc".getBytes());
        UploadFile uploadFile = UploadFile.from(file);
        UploadFile randomFile = UploadFile.of("test.png", "randomrandom.png");

        assertAll(
                () -> assertThat(uploadFile.getUploadName()).isEqualTo("test.png"),
                () -> assertThat(uploadFile.getStorageName().length()).isEqualTo(24),
                () -> assertThat(randomFile.getUploadName()).isEqualTo("test.png"),
                () -> assertThat(randomFile.getStorageName()).isEqualTo("randomrandom.png")
        );
    }
}
