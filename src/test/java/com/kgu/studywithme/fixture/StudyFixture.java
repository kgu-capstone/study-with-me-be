package com.kgu.studywithme.fixture;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyType;
import com.kgu.studywithme.study.domain.participant.Capacity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

@Getter
@RequiredArgsConstructor
public enum StudyFixture {
    TOEIC("토익 스터디", "토익 스터디입니다", LANGUAGE, ONLINE, 4, new HashSet<>(Set.of("A", "B", "C"))),
    TOSS_INTERVIEW("Toss 면접 스터디", "Toss 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE, 4, new HashSet<>(Set.of("A", "B", "C"))),
    SPRING("Spring 스터디", "Spring 스터디입니다", PROGRAMMING, ONLINE, 5, new HashSet<>(Set.of("A", "B", "C"))),
    JPA("JPA 스터디", "JPA 스터디입니다", PROGRAMMING, ONLINE, 3, new HashSet<>(Set.of("A", "B", "C"))),
    REAL_MYSQL("Real MySQL 스터디", "Real MySQL 스터디입니다", PROGRAMMING, ONLINE, 6, new HashSet<>(Set.of("A", "B", "C"))),
    ;

    private final String name;
    private final String description;
    private final Category category;
    private final StudyType type;
    private final int capacity;
    private final Set<String> hashtags;

    public Study toStudy(Member host) {
        return Study.builder()
                .host(host)
                .name(StudyName.from(name))
                .description(Description.from(description))
                .capacity(Capacity.from(capacity))
                .type(type)
                .category(category)
                .hashtags(hashtags)
                .build();
    }
}
