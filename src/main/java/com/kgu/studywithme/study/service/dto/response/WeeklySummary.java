package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;

import java.util.List;

public record WeeklySummary(
        Long id, String title, String content, int week, Period period, StudyMember creator,
        boolean assignmentExists, boolean autoAttendance,
        List<String> attachments, List<WeeklySubmitSummary> submits
) {
    public WeeklySummary(Week week) {
        this(
                week.getId(),
                week.getTitle(),
                week.getContent(),
                week.getWeek(),
                week.getPeriod(),
                new StudyMember(week.getCreator()),
                week.isAssignmentExists(),
                week.isAutoAttendance(),
                transformAttachments(week.getAttachments()),
                transformSubmits(week.getSubmits())
        );
    }

    private static List<String> transformAttachments(List<Attachment> attachments) {
        return attachments.stream()
                .map(Attachment::getLink)
                .toList();
    }

    private static List<WeeklySubmitSummary> transformSubmits(List<Submit> submits) {
        return submits.stream()
                .map(WeeklySubmitSummary::new)
                .toList();
    }
}
