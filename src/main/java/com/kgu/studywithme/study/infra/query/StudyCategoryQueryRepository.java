package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudyCategoryQueryRepository {
    Slice<BasicStudy> findStudyByCategory(StudyCategoryCondition condition, Pageable pageable);
    Slice<BasicStudy> findStudyByRecommend(StudyRecommendCondition condition, Pageable pageable);
}
