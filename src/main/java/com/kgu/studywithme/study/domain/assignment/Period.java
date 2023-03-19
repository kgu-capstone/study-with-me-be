package com.kgu.studywithme.study.domain.assignment;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Period {
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    private Period(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Period of(LocalDateTime startDate, LocalDateTime endDate) {
        validateStartIsBeforeEnd(startDate, endDate);
        validateEndDataIsNotPast(endDate);
        return new Period(startDate, endDate);
    }

    private static void validateStartIsBeforeEnd(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw StudyWithMeException.type(StudyErrorCode.PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE);
        }
    }

    private static void validateEndDataIsNotPast(LocalDateTime endDate) {
        if (endDate.isBefore(LocalDateTime.now())) {
            throw StudyWithMeException.type(StudyErrorCode.PERIOD_END_DATE_MUST_BE_SET_FROM_NOW_ON);
        }
    }
}
