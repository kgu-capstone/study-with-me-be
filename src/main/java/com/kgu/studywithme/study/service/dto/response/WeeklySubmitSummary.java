package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.domain.week.submit.Submit;

public record WeeklySubmitSummary(
        StudyMember participant,
        String submitType,
        String submitFileName,
        String submitLink
) {
    public WeeklySubmitSummary(Submit submit) {
        this(
                new StudyMember(submit.getParticipant()),
                submit.getUpload().getType().getDescription(),
                submit.getUpload().getUploadFileName(),
                submit.getUpload().getLink()
        );
    }
}
