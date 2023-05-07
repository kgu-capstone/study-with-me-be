package com.kgu.studywithme.study.domain;

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
public class GraduationPolicy {
    private static final int DEFAULT_UPDATE_CHANGE = 3;

    @Column(name = "minimum_attendance", nullable = false)
    private int minimumAttendance;

    @Column(name = "policy_update_chance", nullable = false)
    private int updateChance;

    private GraduationPolicy(int minimumAttendance, int updateChance) {
        this.minimumAttendance = minimumAttendance;
        this.updateChance = updateChance;
    }

    public static GraduationPolicy initPolicy(int minimumAttendance) {
        return new GraduationPolicy(minimumAttendance, DEFAULT_UPDATE_CHANGE);
    }

    public GraduationPolicy update(int minimumAttendance) {
        if (this.minimumAttendance == minimumAttendance) {
            return new GraduationPolicy(minimumAttendance, updateChance);
        }

        validateUpdateChangeIsRemain();
        return new GraduationPolicy(minimumAttendance, updateChance - 1);
    }

    private void validateUpdateChangeIsRemain() {
        if (updateChance == 0) {
            throw StudyWithMeException.type(StudyErrorCode.NO_CHANGE_TO_UPDATE_GRADUATION_POLICY);
        }
    }

    public GraduationPolicy resetUpdateChanceForDelegatingStudyHost() {
        return new GraduationPolicy(minimumAttendance, DEFAULT_UPDATE_CHANGE);
    }

    public boolean isGraduationRequirementsFulfilled(int value) {
        return minimumAttendance <= value;
    }
}
