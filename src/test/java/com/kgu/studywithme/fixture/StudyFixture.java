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
    TOEIC("TOEIC 스터디", "TOEIC 스터디입니다", LANGUAGE, ONLINE, null, 10, new HashSet<>(Set.of("언어", "토익", "TOEIC"))),
    TOEFL("TOEFL 스터디", "TOEFL 스터디입니다", LANGUAGE, ONLINE, null, 10, new HashSet<>(Set.of("언어", "토플", "TOEFL"))),
    JAPANESE("일본어 스터디", "일본어 스터디입니다", LANGUAGE, ONLINE, null, 10, new HashSet<>(Set.of("언어", "일본어"))),
    CHINESE("중국어 스터디", "중국어 스터디입니다", LANGUAGE, ONLINE, null, 10, new HashSet<>(Set.of("언어", "중국어"))),
    FRENCH("프랑스어 스터디", "프랑스어 스터디입니다", LANGUAGE, ONLINE, null, 10, new HashSet<>(Set.of("언어", "프랑스어"))),

    TOSS_INTERVIEW("Toss 면접 스터디", "Toss 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE, StudyArea.of("서울특별시", "강남구"), 10, new HashSet<>(Set.of("면접", "토스", "기술 면접"))),
    KAKAO_INTERVIEW("Kakao 면접 스터디", "Kakao 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE, StudyArea.of("경기도", "성남시"), 10, new HashSet<>(Set.of("면접", "카카오", "기술 면접"))),
    NAVER_INTERVIEW("Naver 면접 스터디", "Naver 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE, StudyArea.of("경기도", "성남시"), 10, new HashSet<>(Set.of("면접", "네이버", "기술 면접"))),
    LINE_INTERVIEW("LINE 면접 스터디", "LINE 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE, StudyArea.of("경기도", "성남시"), 10, new HashSet<>(Set.of("면접", "라인", "기술 면접"))),
    GOOGLE_INTERVIEW("Google 면접 스터디", "Google 기술 면접을 대비하기 위한 스터디입니다", INTERVIEW, OFFLINE, StudyArea.of("서울특별시", "강남구"), 10, new HashSet<>(Set.of("면접", "구글", "기술 면접"))),

    SPRING("Spring 스터디", "Spring 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("프로그래밍", "스프링", "Spring", "김영한"))),
    JPA("JPA 스터디", "JPA 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("프로그래밍", "JPA", "Hibernate", "김영한"))),
    REAL_MYSQL("Real MySQL 스터디", "Real MySQL 스터디입니다", PROGRAMMING, OFFLINE, StudyArea.of("서울특별시", "강남구"), 10, new HashSet<>(Set.of("DB", "Real MySQL", "DBA"))),
    KOTLIN("코틀린 스터디", "코틀린 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("프로그래밍", "코틀린", "Kotlin"))),
    NETWORK("네트워크 스터디", "네트워크 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("네트워크", "인프라", "OSI 7 Layer", "TCP/IP"))),
    EFFECTIVE_JAVA("이펙티브 자바 스터디", "이펙티브 자바 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("프로그래밍", "자바", "이펙티브 자바"))),
    AWS("AWS 스터디", "AWS 스터디입니다", PROGRAMMING, OFFLINE, StudyArea.of("서울특별시", "강남구"), 10, new HashSet<>(Set.of("AWS", "클라우드 플랫폼", "배포"))),
    DOCKER("Docker 스터디", "Docker 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("Docker", "컨테이너"))),
    KUBERNETES("Kubernetes 스터디", "Kubernetes 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("Kubernetes", "인프라"))),
    PYTHON("파이썬 스터디", "파이썬 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("프로그래밍", "파이썬", "Python", "Flask"))),
    RUST("러스트 스터디", "러스트 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("프로그래밍", "러스트", "Rust"))),
    OS("운영체제 스터디", "운영체제 스터디입니다", PROGRAMMING, ONLINE, null, 10, new HashSet<>(Set.of("OS", "운영체제", "프로세스와 쓰레드", "데드락"))),
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
