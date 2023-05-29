package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.UploadAssignment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.fixture.AttachmentFixture.*;
import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.PeriodFixture.WEEK_6;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_5;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week 도메인 테스트")
class WeekTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);

    @Test
    @DisplayName("Week을 생성한다")
    void construct() {
        Week week = STUDY_WEEKLY_5.toWeek(STUDY);
        Week weekWithAssignment = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);

        assertAll(
                () -> assertThat(week.getStudy()).isEqualTo(STUDY),
                () -> assertThat(week.getCreator()).isEqualTo(HOST),
                () -> assertThat(week.getTitle()).isEqualTo(STUDY_WEEKLY_5.getTitle()),
                () -> assertThat(week.getContent()).isEqualTo(STUDY_WEEKLY_5.getContent()),
                () -> assertThat(week.getWeek()).isEqualTo(STUDY_WEEKLY_5.getWeek()),
                () -> assertThat(week.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_5.getPeriod().getStartDate()),
                () -> assertThat(week.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_5.getPeriod().getEndDate()),
                () -> assertThat(week.isAssignmentExists()).isFalse(),
                () -> assertThat(week.isAutoAttendance()).isFalse(),
                () -> assertThat(week.getAttachments())
                        .map(Attachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_5.getAttachments()),

                () -> assertThat(weekWithAssignment.getStudy()).isEqualTo(STUDY),
                () -> assertThat(weekWithAssignment.getCreator()).isEqualTo(HOST),
                () -> assertThat(weekWithAssignment.getTitle()).isEqualTo(STUDY_WEEKLY_1.getTitle()),
                () -> assertThat(weekWithAssignment.getContent()).isEqualTo(STUDY_WEEKLY_1.getContent()),
                () -> assertThat(weekWithAssignment.getWeek()).isEqualTo(STUDY_WEEKLY_1.getWeek()),
                () -> assertThat(weekWithAssignment.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_1.getPeriod().getStartDate()),
                () -> assertThat(weekWithAssignment.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_1.getPeriod().getEndDate()),
                () -> assertThat(weekWithAssignment.isAssignmentExists()).isTrue(),
                () -> assertThat(weekWithAssignment.isAutoAttendance()).isTrue(),
                () -> assertThat(weekWithAssignment.getAttachments())
                        .map(Attachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_1.getAttachments())
        );
    }

    @Test
    @DisplayName("Week을 수정한다")
    void update() {
        // given
        Week week = STUDY_WEEKLY_5.toWeek(STUDY);

        // when
        List<UploadAttachment> attachments = List.of(
                UploadAttachment.of(PDF_FILE.getUploadFileName(), PDF_FILE.getLink()),
                UploadAttachment.of(TXT_FILE.getUploadFileName(), TXT_FILE.getLink()),
                UploadAttachment.of(HWP_FILE.getUploadFileName(), HWP_FILE.getLink()),
                UploadAttachment.of(IMG_FILE.getUploadFileName(), IMG_FILE.getLink())
        );
        week.update(
                "title",
                "content",
                WEEK_6.toPeriod(),
                true,
                true,
                attachments
        );

        // then
        assertAll(
                () -> assertThat(week.getStudy()).isEqualTo(STUDY),
                () -> assertThat(week.getCreator()).isEqualTo(HOST),
                () -> assertThat(week.getTitle()).isEqualTo("title"),
                () -> assertThat(week.getContent()).isEqualTo("content"),
                () -> assertThat(week.getWeek()).isEqualTo(STUDY_WEEKLY_5.getWeek()),
                () -> assertThat(week.getPeriod().getStartDate()).isEqualTo(WEEK_6.getStartDate()),
                () -> assertThat(week.getPeriod().getEndDate()).isEqualTo(WEEK_6.getEndDate()),
                () -> assertThat(week.isAssignmentExists()).isTrue(),
                () -> assertThat(week.isAutoAttendance()).isTrue(),
                () -> assertThat(week.getAttachments())
                        .map(Attachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(attachments)
        );
    }

    @Test
    @DisplayName("과제 제출이 필수인 Week에 과제를 제출한다")
    void submitAssignment() {
        // given
        Week week = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);

        // when
        final UploadAssignment hostUploadAssignment = UploadAssignment.withLink("https://google.com");
        final UploadAssignment participantUploadAssignment = UploadAssignment.withLink("https://naver.com");
        week.submitAssignment(HOST, hostUploadAssignment);
        week.submitAssignment(PARTICIPANT, participantUploadAssignment);

        // then
        assertAll(
                () -> assertThat(week.getSubmits()).hasSize(2),
                () -> assertThat(week.getSubmits())
                        .map(Submit::getParticipant)
                        .containsExactlyInAnyOrder(HOST, PARTICIPANT),
                () -> assertThat(week.getSubmits())
                        .map(Submit::getUploadAssignment)
                        .containsExactlyInAnyOrder(hostUploadAssignment, participantUploadAssignment)
        );
    }
}
