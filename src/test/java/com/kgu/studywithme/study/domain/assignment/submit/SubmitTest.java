package com.kgu.studywithme.study.domain.assignment.submit;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.assignment.Assignment;
import com.kgu.studywithme.study.domain.assignment.Period;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Assignment-Submit 도메인 테스트")
class SubmitTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));
    private static final Assignment ASSIGNMENT = Assignment.createAssignment(1, PERIOD, STUDY, HOST, "과제 1", "과제입니다.");

    @Test
    @DisplayName("Submit[With Link]을 생성한다")
    void constructWithLink() {
        final Upload upload = Upload.withLink("https://google.com");
        Submit submit = Submit.submitAssignment(ASSIGNMENT, HOST, upload);

        assertAll(
                () -> assertThat(submit.getAssignment()).isEqualTo(ASSIGNMENT),
                () -> assertThat(submit.getParticipant()).isEqualTo(HOST),
                () -> assertThat(submit.getUpload()).isEqualTo(upload),
                () -> assertThat(submit.getUpload().getLink()).isEqualTo("https://google.com"),
                () -> assertThat(submit.getUpload().getType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("Submit[With File]을 생성한다")
    void constructWithFile() {
        final MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "abc".getBytes());
        final UploadFile uploadFile = UploadFile.from(file);
        final Upload upload = Upload.withFile(uploadFile);
        Submit submit = Submit.submitAssignment(ASSIGNMENT, HOST, upload);

        assertAll(
                () -> assertThat(submit.getAssignment()).isEqualTo(ASSIGNMENT),
                () -> assertThat(submit.getParticipant()).isEqualTo(HOST),
                () -> assertThat(submit.getUpload()).isEqualTo(upload),
                () -> assertThat(submit.getUpload().getFile()).isEqualTo(uploadFile),
                () -> assertThat(submit.getUpload().getType()).isEqualTo(FILE)
        );
    }
}
