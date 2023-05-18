package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week 도메인 {Weekly VO} 테스트")
class WeeklyTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);

    @Test
    @DisplayName("이미 해당 주차가 등록되어 있으며 중복으로 등록할 수 없다")
    void throwExceptionByAlreadyWeekCreated() {
        // given
        final Week week = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);
        Weekly weekly = Weekly.createWeeklyPage();
        weekly.registerWeek(week);

        // when - then
        final Week duplicateWeek = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);
        assertThatThrownBy(() -> weekly.registerWeek(duplicateWeek))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.ALREADY_WEEK_CREATED.getMessage());
    }

    @Test
    @DisplayName("주차를 건너뛰어서 등록할 수 없다 [1주차 -> 3주차]")
    void throwExceptionByWeeklyMustBeSequential() {
        // given
        final Week week = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);
        Weekly weekly = Weekly.createWeeklyPage();
        weekly.registerWeek(week);

        // when - then
        final Week week3 = STUDY_WEEKLY_3.toWeekWithAssignment(STUDY);
        assertThatThrownBy(() -> weekly.registerWeek(week3))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.WEEKLY_MUST_BE_SEQUENTIAL.getMessage());
    }

    @Test
    @DisplayName("스터디 주차를 등록한다")
    void success() {
        final Week week1 = STUDY_WEEKLY_1.toWeekWithAssignment(STUDY);
        final Week week2 = STUDY_WEEKLY_2.toWeekWithAssignment(STUDY);
        final Week week3 = STUDY_WEEKLY_3.toWeekWithAssignment(STUDY);

        Weekly weekly = Weekly.createWeeklyPage();
        weekly.registerWeek(week1);
        weekly.registerWeek(week2);
        weekly.registerWeek(week3);

        assertAll(
                () -> assertThat(weekly.getCount()).isEqualTo(3),
                () -> assertThat(weekly.getWeeks())
                        .map(Week::getWeek)
                        .containsExactly(1, 2, 3)
        );
    }
}
