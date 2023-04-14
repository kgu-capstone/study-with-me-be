package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week 도메인 {Weekly VO} 테스트")
class WeeklyTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));

    @Test
    @DisplayName("이미 해당 주차가 등록되어 있으며 중복으로 등록할 수 없다")
    void registerFailure() {
        // given
        final Week week = Week.createWeek(STUDY, "1번째 주차", "지정된 시간에 줌 접속해주세요", 1, PERIOD);
        Weekly weekly = Weekly.createWeeklyPage();
        weekly.registerWeek(week);

        // when - then
        final Week duplicateWeek = Week.createWeek(STUDY, "2번째 주차", "지정된 시간에 줌 접속해주세요", 1, PERIOD);
        assertThatThrownBy(() -> weekly.registerWeek(duplicateWeek))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.ALREADY_WEEK_CREATED.getMessage());
    }

    @Test
    @DisplayName("스터디 주차를 등록한다")
    void registerSuccess() {
        final Week week = Week.createWeek(STUDY, "첫번째 주차", "지정된 시간에 줌 접속해주세요", 1, PERIOD);
        Weekly weekly = Weekly.createWeeklyPage();
        weekly.registerWeek(week);

        assertAll(
                () -> assertThat(weekly.getCount()).isEqualTo(1),
                () -> assertThat(weekly.getWeeks())
                        .map(Week::getWeek)
                        .contains(1)
        );
    }
}
