package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyFindService 테스트")
class StudyFindServiceTest extends ServiceTest {
    @Autowired
    private StudyFindService studyFindService;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));
    }

    @Test
    @DisplayName("ID(PK)로 스터디를 조회한다")
    void findById() {
        // when
        assertThatThrownBy(() -> studyFindService.findById(study.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> studyFindService.findByIdWithParticipants(study.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> studyFindService.findByIdWithHost(study.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_NOT_FOUND.getMessage());

        Study findStudy1 = studyFindService.findById(study.getId());
        Study findStudy2 = studyFindService.findByIdWithParticipants(study.getId());
        Study findStudy3 = studyFindService.findByIdWithHost(study.getId());

        // then
        assertAll(
                () -> assertThat(findStudy1).isEqualTo(study),
                () -> assertThat(findStudy2).isEqualTo(study),
                () -> assertThat(findStudy3).isEqualTo(study)
        );
    }

    @Test
    @DisplayName("스터디 ID(PK) & 팀장 ID(PK)로 스터디를 조회한다")
    void findByIdAndHostId() {
        // when
        assertThatThrownBy(() -> studyFindService.findByIdAndHostId(study.getId() + 100L, host.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> studyFindService.findByIdAndHostId(study.getId(), host.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> studyFindService.findByIdAndHostId(study.getId() + 100L, host.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_NOT_FOUND.getMessage());

        Study findStudy = studyFindService.findByIdAndHostId(study.getId(), host.getId());

        // then
        assertThat(findStudy).isEqualTo(study);
    }
}
