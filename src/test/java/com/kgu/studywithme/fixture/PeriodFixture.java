package com.kgu.studywithme.fixture;

import com.kgu.studywithme.study.domain.week.Period;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum PeriodFixture {
    WEEK_1(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7)),
    WEEK_2(LocalDateTime.now().plusDays(8), LocalDateTime.now().plusDays(14)),
    WEEK_3(LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(21)),
    WEEK_4(LocalDateTime.now().plusDays(22), LocalDateTime.now().plusDays(28)),
    WEEK_5(LocalDateTime.now().plusDays(29), LocalDateTime.now().plusDays(35)),
    WEEK_6(LocalDateTime.now().plusDays(36), LocalDateTime.now().plusDays(42)),
    ;

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public Period toPeriod() {
        return Period.of(startDate, endDate);
    }
}
