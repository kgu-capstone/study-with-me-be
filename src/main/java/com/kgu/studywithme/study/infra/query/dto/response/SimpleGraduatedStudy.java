package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SimpleGraduatedStudy {
    private final Long id;
    private final String name;
    private final String category;
    private final String thumbnail;
    private final String thumbnailBackground;
    private final StudyReview review;

    @QueryProjection
    public SimpleGraduatedStudy(
            Long id, StudyName name, Category category, StudyThumbnail thumbnail,
            Long reviewId, String content, LocalDateTime writtenDate, LocalDateTime lastModifiedDate
    ) {
        this.id = id;
        this.name = name.getValue();
        this.category = category.getName();
        this.thumbnail = thumbnail.getImageName();
        this.thumbnailBackground = thumbnail.getBackground();
        this.review = (reviewId != null) ? new StudyReview(reviewId, content, writtenDate, lastModifiedDate) : null;
    }
}
