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
        study = studyRepository.save(SPRING.toOnlineStudy(host));
    }

    @Test
    @DisplayName("ID(PK)로 스터디를 조회한다")
    void findByIdWithHost() {
        // when
        Study findStudy1 = studyRepository.findByIdWithHashtags(study.getId()).orElseThrow();
        Study findStudy2 = studyRepository.findByIdWithHost(study.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findStudy1).isEqualTo(study),
                () -> assertThat(findStudy2).isEqualTo(study)
        );
    }

    @Test
    @DisplayName("스터디 ID + 팀장 ID로 스터디를 조회한다")
    void findByIdAndHostId() {
        // when
        Study findStudy = studyRepository.findByIdAndHostId(study.getId(), host.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findStudy).isEqualTo(study),
                () -> assertThat(findStudy.getHost()).isEqualTo(host)
        );
    }

    @Test
    @DisplayName("스터디명에 해당하는 스터디가 존재하는지 확인한다")
    void existsByName() {
        // when
        boolean actual1 = studyRepository.existsByName(study.getName());
        boolean actual2 = studyRepository.existsByName(StudyName.from(study.getNameValue() + "diff"));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
    
    @Test
    @DisplayName("스터디 ID & 팀장 ID에 해당하는 스터디가 존재하는지 확인한다")
    void existsByIdAndHostId() {
        // when
        boolean actual1 = studyRepository.existsByIdAndParticipantsHostId(study.getId(), host.getId());
        boolean actual2 = studyRepository.existsByIdAndParticipantsHostId(study.getId() + 100L, host.getId());
        boolean actual3 = studyRepository.existsByIdAndParticipantsHostId(study.getId(), host.getId() + 100L);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isFalse()
        );
    }
}
