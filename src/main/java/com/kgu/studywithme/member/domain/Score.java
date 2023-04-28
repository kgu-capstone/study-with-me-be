package com.kgu.studywithme.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Score {
    // 초기 세팅값
    private static final int DEFAULT_INIT_VALUE = 100;
    private static final int MINIMUM = 0;
    private static final int MAXIMUM = 100;

    // 출석 관련 점수
    private static final int ATTENDANCE = 1;
    private static final int LATE = 1;
    private static final int ABSENCE = 5;

    @Column(name = "score", nullable = false)
    private int value;

    private Score(int value) {
        this.value = value < MINIMUM ? MINIMUM : Math.min(value, MAXIMUM);
    }

    public static Score initScore() {
        return new Score(DEFAULT_INIT_VALUE);
    }

    public Score applyAttendance() {
        return new Score(value + ATTENDANCE);
    }

    public Score applyLate() {
        return new Score(value - LATE);
    }

    public Score applyAbsence() {
        return new Score(value - ABSENCE);
    }

    public Score updateAttendanceToLate() {
        return new Score(value - ATTENDANCE - LATE);
    }

    public Score updateAttendanceToAbsence() {
        return new Score(value - ATTENDANCE - ABSENCE);
    }

    public Score updateLateToAttendance() {
        return new Score(value + LATE + ATTENDANCE);
    }

    public Score updateLateToAbsence() {
        return new Score(value + LATE - ABSENCE);
    }

    public Score updateAbsenceToAttendance() {
        return new Score(value + ABSENCE + ATTENDANCE);
    }

    public Score updateAbsenceToLate() {
        return new Score(value + ABSENCE - LATE);
    }
}
