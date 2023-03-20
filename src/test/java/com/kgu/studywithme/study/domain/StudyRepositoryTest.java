package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> StudyRepository 테스트")
class StudyRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toStudy(host));
    }

    @Test
    @DisplayName("스터디를 조회한다 [With Host]")
    void findByIdWithHost() {
        // when
        Study findStudy = studyRepository.findByIdWithHost(study.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findStudy).isEqualTo(study),
                () -> assertThat(findStudy.getHost()).isEqualTo(host)
        );
    }
}
