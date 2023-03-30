package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.RecruitmentStatus;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyType;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BasicStudy {
    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final String type;
    private final String recruitmentStatus;
    private final long currentMembers;
    private final int maxMembers;
    private final LocalDateTime registerDate;
    private final long favoriteCount;
    private final long reviewCount;
    private List<String> hashtags;

    @Builder
    @QueryProjection
    public BasicStudy(Long id, StudyName name, Description description, Category category, StudyType type, RecruitmentStatus recruitmentStatus,
                      long currentMembers, Capacity capacity, LocalDateTime registerDate, long favoriteCount, long reviewCount) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.category = category.getName();
        this.type = type.getDescription();
        this.recruitmentStatus = recruitmentStatus.getDescription();
        this.currentMembers = currentMembers;
        this.maxMembers = capacity.getValue();
        this.registerDate = registerDate;
        this.favoriteCount = favoriteCount;
        this.reviewCount = reviewCount;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
}
