package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudyCategoryQueryRepository {
    Slice<BasicStudy> findStudyWithCondition(StudyCategoryCondition condition, Pageable pageable);
}
