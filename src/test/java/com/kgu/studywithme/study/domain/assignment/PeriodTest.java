package com.kgu.studywithme.study.domain.assignment;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Assignment 도메인 {Period VO} 테스트")
class PeriodTest {
    @Test
    @DisplayName("Period을 생성한다")
    void constructSuccess() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        Period period = Period.of(startDate, endDate);

        assertAll(
                () -> assertThat(period.getStartDate()).isEqualTo(startDate),
                () -> assertThat(period.getEndDate()).isEqualTo(endDate)
        );
    }
    
    @Test
    @DisplayName("시작일이 종료일보다 늦는다면 Period 생성에 실패한다")
    void failureByReversedDate() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> Period.of(startDate, endDate))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE.getMessage());
    }
    
    @Test
    @DisplayName("종료일이 현재 날짜 이전이면 Period 생성에 실패한다")
    void failureByInvalidEndDate() {
        final LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        assertThatThrownBy(() -> Period.of(startDate, endDate))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.PERIOD_END_DATE_MUST_BE_SET_FROM_NOW_ON.getMessage());
    }
}
