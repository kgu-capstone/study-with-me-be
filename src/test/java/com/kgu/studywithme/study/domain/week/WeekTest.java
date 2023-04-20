package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.Upload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
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

                () -> assertThat(weekWithAssignment.getStudy()).isEqualTo(STUDY),
                () -> assertThat(weekWithAssignment.getCreator()).isEqualTo(HOST),
                () -> assertThat(weekWithAssignment.getTitle()).isEqualTo(STUDY_WEEKLY_1.getTitle()),
                () -> assertThat(weekWithAssignment.getContent()).isEqualTo(STUDY_WEEKLY_1.getContent()),
                () -> assertThat(weekWithAssignment.getWeek()).isEqualTo(STUDY_WEEKLY_1.getWeek()),
                () -> assertThat(weekWithAssignment.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_1.getPeriod().getStartDate()),
                () -> assertThat(weekWithAssignment.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_1.getPeriod().getEndDate()),
                () -> assertThat(weekWithAssignment.isAssignmentExists()).isTrue(),
                () -> assertThat(weekWithAssignment.isAutoAttendance()).isTrue()
        );
    }

    @Test
    @DisplayName("과제 제출이 필수인 Week에 과제를 제출한다")
    void submitAssignment() {
        // given
        Week week = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);

        // when
        final Upload hostUpload = Upload.withLink("https://google.com");
        final Upload participantUpload = Upload.withLink("https://naver.com");
        week.submitAssignment(HOST, hostUpload);
        week.submitAssignment(PARTICIPANT, participantUpload);

        // then
        assertAll(
                () -> assertThat(week.getSubmits()).hasSize(2),
                () -> assertThat(week.getSubmits())
                        .map(Submit::getParticipant)
                        .containsExactlyInAnyOrder(HOST, PARTICIPANT),
                () -> assertThat(week.getSubmits())
                        .map(Submit::getUpload)
                        .containsExactlyInAnyOrder(hostUpload, participantUpload)
        );
    }
}
