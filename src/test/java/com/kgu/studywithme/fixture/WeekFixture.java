package com.kgu.studywithme.fixture;

import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.Week;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kgu.studywithme.fixture.AttachmentFixture.*;
import static com.kgu.studywithme.fixture.PeriodFixture.*;

@Getter
@RequiredArgsConstructor
public enum WeekFixture {
    STUDY_WEEKLY_1(
            "Week 1", "지정된 기간까지 과제 제출해주세요.",
            1, WEEK_1, true, true,
            List.of(IMG_FILE.getLink(), PDF_FILE.getLink())
    ),
    STUDY_WEEKLY_2(
            "Week 2", "지정된 기간까지 과제 제출해주세요.",
            2, WEEK_2, true, true,
            List.of(IMG_FILE.getLink(), HWP_FILE.getLink())
    ),
    STUDY_WEEKLY_3(
            "Week 3", "지정된 기간까지 과제 제출해주세요.",
            3, WEEK_3, true, true,
            List.of(PDF_FILE.getLink())
    ),
    STUDY_WEEKLY_4(
            "Week 4", "지정된 기간까지 과제 제출해주세요.",
            4, WEEK_4, true, true,
            List.of(TXT_FILE.getLink())
    ),
    STUDY_WEEKLY_5(
            "Week 5", "지정된 시간까지 다들 줌에 접속해주세요.",
            5, WEEK_5, false, false,
            List.of()
    ),
    STUDY_WEEKLY_6(
            "Week 6", "지정된 시간까지 다들 줌에 접속해주세요.",
            6, WEEK_6, false, false,
            List.of()
    ),
    ;

    private final String title;
    private final String content;
    private final int week;
    private final PeriodFixture period;
    private final boolean assignmentExists;
    private final boolean autoAttendance;
    private final List<String> attachments;

    public Week toWeek(Study study) {
        return Week.createWeek(study, title, content, week, period.toPeriod(), attachments);
    }

    public Week toWeekWithAssignment(Study study) {
        return Week.createWeekWithAssignment(
                study, title, content, week, period.toPeriod(), assignmentExists, autoAttendance, attachments
        );
    }
}
