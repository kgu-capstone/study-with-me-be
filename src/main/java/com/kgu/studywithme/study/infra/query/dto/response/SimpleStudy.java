package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.StudyName;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class SimpleStudy {
    private final Long id;
    private final String name;
    private final String category;

    @QueryProjection
    public SimpleStudy(Long id, StudyName name, Category category) {
        this.id = id;
        this.name = name.getValue();
        this.category = category.getName();
    }
}
