package com.kgu.studywithme.study.domain.assignment;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Assignment 도메인 {Assignments VO} 테스트")
class AssignmentsTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));

    @Test
    @DisplayName("스터디에 대한 과제를 등록한다")
    void registerSuccess() {
        final Assignment assignment = Assignment.createAssignment(1, PERIOD, STUDY, HOST, "첫번째 과제", "과제 입니다.");
        Assignments assignments = new Assignments();
        assignments.registerAssignment(assignment);
        
        assertAll(
                () -> assertThat(assignments.getCount()).isEqualTo(1),
                () -> assertThat(assignments.getAssignments())
                        .map(Assignment::getWeek)
                        .contains(1)
        );
    }
    
    @Test
    @DisplayName("이미 해당 주차에 과제가 등록되어 있으면 동일한 주차에 추가적인 과제는 등록할 수 없다")
    void registerFailure() {
        // given
        final Assignment assignment = Assignment.createAssignment(1, PERIOD, STUDY, HOST, "첫번째 과제", "과제 입니다.");
        Assignments assignments = new Assignments();
        assignments.registerAssignment(assignment);

        // when - then
        final Assignment duplicateAssignment = Assignment.createAssignment(1, PERIOD, STUDY, PARTICIPANT, "두번째 과제", "과제 입니다.");
        assertThatThrownBy(() -> assignments.registerAssignment(duplicateAssignment))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.ALREADY_ASSIGNMENT_EXISTS_PER_WEEK.getMessage());
    }
}
