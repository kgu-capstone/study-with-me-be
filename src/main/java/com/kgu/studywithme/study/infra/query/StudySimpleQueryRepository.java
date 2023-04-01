package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;

import java.util.List;

public interface StudySimpleQueryRepository {
    List<BasicHashtag> findHashtags();
    List<String> findHashtagsByStudyId(Long studyId);
}
