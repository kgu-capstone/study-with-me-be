package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BasicStudy {
    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final String thumbnail;
    private final String thumbnailBackground;
    private final String type;
    private final String recruitmentStatus;
    private final int currentMembers;
    private final int maxMembers;
    private final LocalDateTime registerDate;
    private List<String> hashtags;
    private List<Long> favoriteMarkingMembers;

    @QueryProjection
    public BasicStudy(Long id, StudyName name, Description description, Category category,
                      StudyThumbnail thumbnail, StudyType type, RecruitmentStatus recruitmentStatus,
                      int currentMembers, Capacity capacity, LocalDateTime registerDate) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.category = category.getName();
        this.thumbnail = thumbnail.getImageName();
        this.thumbnailBackground = thumbnail.getBackground();
        this.type = type.getDescription();
        this.recruitmentStatus = recruitmentStatus.getDescription();
        this.currentMembers = currentMembers + 1;
        this.maxMembers = capacity.getValue();
        this.registerDate = registerDate;
    }

    public void applyHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyFavoriteMarkingMembers(List<Long> favoriteMarkingMembers) {
        this.favoriteMarkingMembers = favoriteMarkingMembers;
    }
}
