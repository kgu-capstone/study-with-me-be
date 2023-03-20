package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> ParticipationService 테스트")
class ParticipationServiceTest extends ServiceTest {
    @Autowired
    private ParticipationService participationService;

    @Nested
    @DisplayName("스터디 참여 신청")
    class apply {
        private Member host;
        private Member applier;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            applier = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toStudy(host));
        }

        @Test
        @DisplayName("모집이 마감된 스터디에는 참여 신청을 할 수 없다")
        void failureByRecruitmentCompleted() {
            // given
            study.completeRecruitment();

            // when - then
            assertThatThrownBy(() -> participationService.apply(study.getId(), applier.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.RECRUITMENT_IS_COMPLETE.getMessage());
        }
        
        @Test
        @DisplayName("스터디 팀장은 참여 신청을 할 수 없다")
        void failureByHost() {
            assertThatThrownBy(() -> participationService.apply(study.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("이미 참여 신청을 했거나 참여중이라면 중복으로 참여 신청을 할 수 없다")
        void failureByAlreadyApply() {
            // given
            study.applyParticipation(applier);

            // when - then
            assertThatThrownBy(() -> participationService.apply(study.getId(), applier.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("참여 신청에 성공한다")
        void success() {
            // when
            participationService.apply(study.getId(), applier.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants().size()).isEqualTo(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactly(host, applier),
                    () -> assertThat(findStudy.getApproveParticipants().size()).isEqualTo(1),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactly(host)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 승인")
    class approve {
        private Member host;
        private Member applier;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            applier = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 승인을 할 수 없다")
        void failureByStudyClosed() {
            // given
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.approve(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }
        
        @Test
        @DisplayName("참여 신청자가 아니면 참여 승인을 할 수 없다")
        void failureByAnonymousMember() {
            assertThatThrownBy(() -> participationService.approve(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }
        
        @Test
        @DisplayName("참여 인원이 꽉 찼다면 더이상 참여 승인을 할 수 없다")
        void failureByAlreadyCapacityFull() {
            // given
            study.applyParticipation(applier);
            makeCapacityFull(study);
            
            // when - then
            assertThatThrownBy(() -> participationService.approve(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.STUDY_CAPACITY_IS_FULL.getMessage());
        }

        private void makeCapacityFull(Study study) {
            ReflectionTestUtils.setField(study.getCapacity(), "value", 1);
        }

        @Test
        @DisplayName("참여 승인에 성공한다")
        void success() {
            // given
            study.applyParticipation(applier);

            // when
            participationService.approve(study.getId(), applier.getId(), host.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants().size()).isEqualTo(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactly(host, applier),
                    () -> assertThat(findStudy.getApproveParticipants().size()).isEqualTo(2),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactly(host, applier)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절")
    class reject {
        private Member host;
        private Member applier;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            applier = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 거절을 할 수 없다")
        void failureByStudyClosed() {
            // given
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.reject(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("참여 신청자가 아니면 참여 거절을 할 수 없다")
        void failureByAnonymousMember() {
            assertThatThrownBy(() -> participationService.reject(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }

        @Test
        @DisplayName("참여 거절에 성공한다")
        void success() {
            // given
            study.applyParticipation(applier);

            // when
            participationService.reject(study.getId(), applier.getId(), host.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants().size()).isEqualTo(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactly(host, applier),
                    () -> assertThat(findStudy.getApproveParticipants().size()).isEqualTo(1),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactly(host)
            );
        }
    }
}
