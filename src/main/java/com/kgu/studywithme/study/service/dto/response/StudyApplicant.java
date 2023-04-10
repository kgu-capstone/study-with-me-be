package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.StudyApplicantInformation;

import java.util.List;

public record StudyApplicant(List<StudyApplicantInformation> applicants) {
}
