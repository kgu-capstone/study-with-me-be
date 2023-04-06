package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;

import java.util.List;

public interface StudyInformationQueryRepository {
    List<NoticeInformation> findNoticeWithCommentsByStudyId(Long studyId);
}
