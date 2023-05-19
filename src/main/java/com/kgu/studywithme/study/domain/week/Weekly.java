package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Weekly {
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Week> weeks = new ArrayList<>();

    public static Weekly createWeeklyPage() {
        return new Weekly();
    }

    public void registerWeek(Week week) {
        validateUniqueWeek(week.getWeek());
        validatePreviousWeeklyExistence(week.getWeek());
        weeks.add(week);
    }

    private void validateUniqueWeek(int week) {
        if (isAlreadyExistsPerWeek(week)) {
            throw StudyWithMeException.type(StudyErrorCode.ALREADY_WEEK_CREATED);
        }
    }

    private boolean isAlreadyExistsPerWeek(int week) {
        return weeks.stream()
                .anyMatch(weekInfo -> weekInfo.getWeek() == week);
    }

    private void validatePreviousWeeklyExistence(int week) {
        if (week != 1 && isPreviousWeeklyNotExists(week)) {
            throw StudyWithMeException.type(StudyErrorCode.WEEKLY_MUST_BE_SEQUENTIAL);
        }
    }

    private boolean isPreviousWeeklyNotExists(int week) {
        return weeks.stream()
                .noneMatch(weekInfo -> weekInfo.getWeek() == week - 1);
    }

    public int getCount() {
        return weeks.size();
    }
}
