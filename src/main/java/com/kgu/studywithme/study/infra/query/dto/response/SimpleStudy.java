package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class SimpleStudy {
    private final Long id;
    private final String name;
    private final String category;
    private final String thumbnail;
    private final String thumbnailBackground;

    @QueryProjection
    public SimpleStudy(Long id, StudyName name, Category category, StudyThumbnail thumbnail) {
        this.id = id;
        this.name = name.getValue();
        this.category = category.getName();
        this.thumbnail = thumbnail.getImageName();
        this.thumbnailBackground = thumbnail.getBackground();
    }
}
