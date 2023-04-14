package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.Upload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week 도메인 테스트")
class WeekTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));

    @Test
    @DisplayName("Week을 생성한다")
    void construct() {
        Week week = Week.createWeek(STUDY, "Week 1", "지정된 시간까지 줌에 접속해주세요", 1, PERIOD);
        Week weekWithAssignment = Week.createWeekWithAssignment(
                STUDY, "Week 1", "지정된 기간까지 과제를 제출하세요", 1, PERIOD, true, true
        );

        assertAll(
                () -> assertThat(week.getStudy()).isEqualTo(STUDY),
                () -> assertThat(week.getCreator()).isEqualTo(HOST),
                () -> assertThat(week.getTitle()).isEqualTo("Week 1"),
                () -> assertThat(week.getContent()).isEqualTo("지정된 시간까지 줌에 접속해주세요"),
                () -> assertThat(week.getWeek()).isEqualTo(1),
                () -> assertThat(week.getPeriod()).isEqualTo(PERIOD),
                () -> assertThat(week.isAssignmentExists()).isFalse(),
                () -> assertThat(week.isAutoAttendance()).isFalse(),

                () -> assertThat(weekWithAssignment.getStudy()).isEqualTo(STUDY),
                () -> assertThat(weekWithAssignment.getCreator()).isEqualTo(HOST),
                () -> assertThat(weekWithAssignment.getTitle()).isEqualTo("Week 1"),
                () -> assertThat(weekWithAssignment.getContent()).isEqualTo("지정된 기간까지 과제를 제출하세요"),
                () -> assertThat(weekWithAssignment.getWeek()).isEqualTo(1),
                () -> assertThat(weekWithAssignment.getPeriod()).isEqualTo(PERIOD),
                () -> assertThat(weekWithAssignment.isAssignmentExists()).isTrue(),
                () -> assertThat(weekWithAssignment.isAutoAttendance()).isTrue()
        );
    }

    @Test
    @DisplayName("과제 제출이 필수인 Week에 과제를 제출한다")
    void submit() {
        // given
        Week week = Week.createWeekWithAssignment(
                STUDY, "Week 1", "지정된 기간까지 과제를 제출하세요", 1, PERIOD, true, true
        );

        // when
        final Upload hostUpload = Upload.withLink("https://google.com");
        final Upload participantUpload = Upload.withLink("https://naver.com");
        week.submitAssignment(HOST, hostUpload);
        week.submitAssignment(PARTICIPANT, participantUpload);

        // then
        assertAll(
                () -> assertThat(week.getSubmits().size()).isEqualTo(2),
                () -> assertThat(week.getSubmits())
                        .map(Submit::getParticipant)
                        .containsExactly(HOST, PARTICIPANT),
                () -> assertThat(week.getSubmits())
                        .map(Submit::getUpload)
                        .containsExactly(hostUpload, participantUpload)
        );
    }
}
