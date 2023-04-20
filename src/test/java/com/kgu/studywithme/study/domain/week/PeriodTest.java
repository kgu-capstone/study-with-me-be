package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week 도메인 {Period VO} 테스트")
class PeriodTest {
    @Test
    @DisplayName("시작일이 종료일보다 늦는다면 Period 생성에 실패한다")
    void throwExceptionByPeriodStartDateMustBeBeforeEndDate() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> Period.of(startDate, endDate))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE.getMessage());
    }

    @Test
    @DisplayName("Period을 생성한다")
    void construct() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        final Period period = Period.of(startDate, endDate);

        assertAll(
                () -> assertThat(period.getStartDate()).isEqualTo(startDate),
                () -> assertThat(period.getEndDate()).isEqualTo(endDate)
        );
    }

    @Test
    @DisplayName("주어진 날짜가 Period의 StartDate ~ EndDate 사이에 포함되는지 확인한다")
    void isDateWithInRange() {
        // given
        final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        final Period period = Period.of(startDate, endDate);

        // when
        boolean actual1 = period.isDateWithInRange(LocalDateTime.now().plusDays(4));
        boolean actual2 = period.isDateWithInRange(LocalDateTime.now().plusDays(8));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
