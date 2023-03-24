package com.kgu.studywithme.fixture;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.*;
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
    TOEIC("토익 스터디", "토익 스터디입니다", LANGUAGE, ONLINE,
            null, 4, new HashSet<>(Set.of("A", "B", "C"))),
    TOSS_INTERVIEW("Toss 면접 스터디", "Toss 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE,
            StudyArea.of("서울특별시", "강남구"), 4, new HashSet<>(Set.of("A", "B", "C"))),
    SPRING("Spring 스터디", "Spring 스터디입니다", PROGRAMMING, ONLINE,
            null, 5, new HashSet<>(Set.of("A", "B", "C"))),
    JPA("JPA 스터디", "JPA 스터디입니다", PROGRAMMING, ONLINE,
            null, 3, new HashSet<>(Set.of("A", "B", "C"))),
    REAL_MYSQL("Real MySQL 스터디", "Real MySQL 스터디입니다", PROGRAMMING, ONLINE,
            null, 6, new HashSet<>(Set.of("A", "B", "C"))),
    ;

    private final String name;
    private final String description;
    private final Category category;
    private final StudyType type;
    private final StudyArea area;
    private final int capacity;
    private final Set<String> hashtags;

    public Study toOnlineStudy(Member host) {
        return Study.createOnlineStudy(host, StudyName.from(name), Description.from(description), Capacity.from(capacity), category, type, hashtags);
    }

    public Study toOfflineStudy(Member host) {
        return Study.createOfflineStudy(host, StudyName.from(name), Description.from(description), Capacity.from(capacity), category, type, area, hashtags);
    }
}
