package com.kgu.studywithme.study.domain.assignment;

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
public class Assignments {
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Assignment> assignments = new ArrayList<>();

    public static Assignments createAssignmentsPage() {
        return new Assignments();
    }

    public void registerAssignment(Assignment assignment) {
        validateUniqueAssignmentPerWeek(assignment.getWeek());
        assignments.add(assignment);
    }

    private void validateUniqueAssignmentPerWeek(int week) {
        if (isAlreadyExistsAssignmentPerWeek(week)) {
            throw StudyWithMeException.type(StudyErrorCode.ALREADY_ASSIGNMENT_EXISTS_PER_WEEK);
        }
    }

    private boolean isAlreadyExistsAssignmentPerWeek(int week) {
        return assignments.stream()
                .anyMatch(assignment -> assignment.getWeek() == week);
    }

    public int getCount() {
        return assignments.size();
    }
}
