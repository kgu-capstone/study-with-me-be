package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Capacity {
    private static final int MINIMUM = 2;
    private static final int MAXIMUM = 10;

    @Column(name = "capacity", nullable = false)
    private int value;

    private Capacity(int value) {
        this.value = value;
    }

    public static Capacity from(int value) {
        validateCapacityIsInRange(value);
        return new Capacity(value);
    }

    private static void validateCapacityIsInRange(int value) {
        if (isOutOfRange(value)) {
            throw StudyWithMeException.type(StudyErrorCode.CAPACITY_OUT_OF_RANGE);
        }
    }

    private static boolean isOutOfRange(int capacity) {
        return (capacity < MINIMUM) || (MAXIMUM < capacity);
    }

    public boolean isEqualOrOver(int compareValue) {
        return value <= compareValue;
    }
}
