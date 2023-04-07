package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;

import java.util.List;

public interface StudyInformationQueryRepository {
    int getGraduatedParticipantCountByStudyId(Long studyId);
    List<ReviewInformation> findReviewByStudyId(Long studyId);
    List<NoticeInformation> findNoticeWithCommentsByStudyId(Long studyId);
}
