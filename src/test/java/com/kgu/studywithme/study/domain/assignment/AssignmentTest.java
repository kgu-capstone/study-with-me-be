package com.kgu.studywithme.study.domain.assignment;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.assignment.submit.Submit;
import com.kgu.studywithme.study.domain.assignment.submit.Upload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Assignment 도메인 테스트")
class AssignmentTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));

    @Test
    @DisplayName("Assignment를 생성한다")
    void construct() {
        Assignment assignment = Assignment.createAssignment(1, PERIOD, STUDY, HOST, "첫번째 과제", "과제 입니다.");

        assertAll(
                () -> assertThat(assignment.getWeek()).isEqualTo(1),
                () -> assertThat(assignment.getPeriod()).isEqualTo(PERIOD),
                () -> assertThat(assignment.getStudy()).isEqualTo(STUDY),
                () -> assertThat(assignment.getCreator()).isEqualTo(HOST),
                () -> assertThat(assignment.getTitle()).isEqualTo("첫번째 과제"),
                () -> assertThat(assignment.getContent()).isEqualTo("과제 입니다.")
        );
    }

    @Test
    @DisplayName("과제에 대한 결과물을 제출한다")
    void submit() {
        // given
        Assignment assignment = Assignment.createAssignment(1, PERIOD, STUDY, HOST, "첫번째 과제", "과제 입니다.");

        // when
        final Upload hostUpload = Upload.withLink("https://google.com");
        final Upload participantUpload = Upload.withLink("https://naver.com");
        assignment.submit(HOST, hostUpload);
        assignment.submit(PARTICIPANT, participantUpload);

        // then
        assertAll(
                () -> assertThat(assignment.getSubmits().size()).isEqualTo(2),
                () -> assertThat(assignment.getSubmits())
                        .map(Submit::getParticipant)
                        .containsExactly(HOST, PARTICIPANT),
                () -> assertThat(assignment.getSubmits())
                        .map(Submit::getUpload)
                        .containsExactly(hostUpload, participantUpload)
        );
    }
}
