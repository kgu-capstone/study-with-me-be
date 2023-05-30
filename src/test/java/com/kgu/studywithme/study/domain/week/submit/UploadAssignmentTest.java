package com.kgu.studywithme.study.domain.week.submit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Submit 도메인 {UploadAssignment VO} 테스트")
class UploadAssignmentTest {
    @Test
    @DisplayName("Notion, Blog 등 링크를 통해서 과제를 제출한다")
    void constructWithLink() {
        UploadAssignment uploadAssignment = UploadAssignment.withLink("https://notion.com");

        assertAll(
                () -> assertThat(uploadAssignment.getUploadFileName()).isNull(),
                () -> assertThat(uploadAssignment.getLink()).isEqualTo("https://notion.com"),
                () -> assertThat(uploadAssignment.getType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("파일 업로드를 통해서 과제를 제출한다")
    void constructWithFile() {
        UploadAssignment uploadAssignment = UploadAssignment.withFile("hello.pdf", "uuid.pdf");

        assertAll(
                () -> assertThat(uploadAssignment.getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(uploadAssignment.getLink()).isEqualTo("uuid.pdf"),
                () -> assertThat(uploadAssignment.getType()).isEqualTo(FILE)
        );
    }
}
