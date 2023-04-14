package com.kgu.studywithme.study.domain.week.submit;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Submit 도메인 테스트")
class SubmitTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));
    private static final Week WEEK = Week.createWeekWithAssignment(
            STUDY, "1번째 주차", "지정된 기간까지 과제 제출해주세요", 1, PERIOD, true, true
    );

    @Test
    @DisplayName("Submit[With Link]을 생성한다")
    void constructWithLink() {
        final Upload upload = Upload.withLink("https://notion.com");
        Submit submit = Submit.submitAssignment(WEEK, HOST, upload);

        assertAll(
                () -> assertThat(submit.getWeek()).isEqualTo(WEEK),
                () -> assertThat(submit.getParticipant()).isEqualTo(HOST),
                () -> assertThat(submit.getUpload()).isEqualTo(upload),
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
                () -> assertThat(submit.getUpload()).isEqualTo(upload),
                () -> assertThat(submit.getUpload().getLink()).isEqualTo("file_upload_link"),
                () -> assertThat(submit.getUpload().getType()).isEqualTo(FILE)
        );
    }
}
