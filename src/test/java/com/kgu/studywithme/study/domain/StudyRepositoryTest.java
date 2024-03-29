package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
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
        Study findStudy1 = studyRepository.findByIdWithParticipants(study.getId()).orElseThrow();
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
        Study findStudy1 = studyRepository.findByIdAndHostId(study.getId(), host.getId()).orElseThrow();
        Study findStudy2 = studyRepository.findByIdAndHostIdWithParticipants(study.getId(), host.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findStudy1).isEqualTo(study),
                () -> assertThat(findStudy2).isEqualTo(study)
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
    @DisplayName("해당 스터디명을 사용하는 다른 스터디가 존재하는지 확인한다")
    void existsByNameAndIdNot() {
        // given
        Study another = studyRepository.save(JPA.toOnlineStudy(host));

        // when
        boolean actual1 = studyRepository.existsByNameAndIdNot(another.getName(), study.getId());
        boolean actual2 = studyRepository.existsByNameAndIdNot(another.getName(), another.getId());

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
