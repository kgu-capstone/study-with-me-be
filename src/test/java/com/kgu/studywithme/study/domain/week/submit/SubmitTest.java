package com.kgu.studywithme.study.domain.week.submit;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.Week;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Submit 도메인 테스트")
class SubmitTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Week WEEK = STUDY_WEEKLY_1.toWeek(STUDY);

    @Test
    @DisplayName("Submit[With Link]을 생성한다")
    void constructWithLink() {
        final Upload upload = Upload.withLink("https://notion.com");
        Submit submit = Submit.submitAssignment(WEEK, HOST, upload);

        assertAll(
                () -> assertThat(submit.getWeek()).isEqualTo(WEEK),
                () -> assertThat(submit.getParticipant()).isEqualTo(HOST),
                () -> assertThat(submit.getUpload().getLink()).isEqualTo("https://notion.com"),
                () -> assertThat(submit.getUpload().getType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("Submit[With File]을 생성한다")
    void constructWithFile() {
        final Upload upload = Upload.withFile("file_upload_link");
        Submit submit = Submit.submitAssignment(WEEK, HOST, upload);

        assertAll(
                () -> assertThat(submit.getWeek()).isEqualTo(WEEK),
                () -> assertThat(submit.getParticipant()).isEqualTo(HOST),
                () -> assertThat(submit.getUpload().getLink()).isEqualTo("file_upload_link"),
                () -> assertThat(submit.getUpload().getType()).isEqualTo(FILE)
        );
    }

    @Test
    @DisplayName("업로드한 과제를 수정한다")
    void editUpload() {
        // given
        final Upload upload = Upload.withFile("file_upload_link");
        Submit submit = Submit.submitAssignment(WEEK, HOST, upload);

        // when
        final Upload newUpload = Upload.withLink("https://notion.so");
        submit.editUpload(newUpload);

        // then
        assertAll(
                () -> assertThat(submit.getWeek()).isEqualTo(WEEK),
                () -> assertThat(submit.getParticipant()).isEqualTo(HOST),
                () -> assertThat(submit.getUpload().getLink()).isEqualTo(newUpload.getLink()),
                () -> assertThat(submit.getUpload().getType()).isEqualTo(newUpload.getType())
        );
    }
}
