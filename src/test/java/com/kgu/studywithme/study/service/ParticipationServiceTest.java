package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.event.StudyApprovedEvent;
import com.kgu.studywithme.study.event.StudyGraduatedEvent;
import com.kgu.studywithme.study.event.StudyRejectedEvent;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@RecordApplicationEvents
@DisplayName("Study [Service Layer] -> ParticipationService 테스트")
class ParticipationServiceTest extends ServiceTest {
    @Autowired
    private ParticipationService participationService;

    @Autowired
    private ApplicationEvents events;

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
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("모집이 마감된 스터디에는 참여 신청을 할 수 없다")
        void throwExceptionByRecruitmentIsComplete() {
            // given
            study.completeRecruitment();

            // when - then
            assertThatThrownBy(() -> participationService.apply(study.getId(), applier.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.RECRUITMENT_IS_COMPLETE.getMessage());
        }
        
        @Test
        @DisplayName("스터디 팀장은 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsHost() {
            assertThatThrownBy(() -> participationService.apply(study.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("이미 참여 신청을 했거나 참여중이라면 중복으로 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsAlreadyParticipate() {
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
                    () -> assertThat(findStudy.getParticipants()).hasSize(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactlyInAnyOrder(host, applier),
                    () -> assertThat(findStudy.getApproveParticipants()).hasSize(1),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactlyInAnyOrder(host)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 신청 취소")
    class applyCancel {
        private Member host;
        private Member applier;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            applier = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("참여 신청자가 아니면 참여 신청을 취소할 수 없다")
        void throwExceptionByMemberIsNotApplier() {
            assertThatThrownBy(() -> participationService.applyCancel(study.getId(), applier.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }

        @Test
        @DisplayName("참여 신청 취소에 성공한다")
        void success() {
            // given
            study.applyParticipation(applier);

            // when
            participationService.applyCancel(study.getId(), applier.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants()).hasSize(1),
                    () -> assertThat(findStudy.getApproveParticipants()).hasSize(1)
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
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 승인을 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() {
            // given
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.approve(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }
        
        @Test
        @DisplayName("참여 신청자가 아니면 참여 승인을 할 수 없다")
        void throwExceptionByMemberIsNotApplier() {
            assertThatThrownBy(() -> participationService.approve(study.getId(), applier.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }
        
        @Test
        @DisplayName("참여 인원이 꽉 찼다면 더이상 참여 승인을 할 수 없다")
        void throwExceptionByStudyCapacityIsFull() {
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
                    () -> assertThat(findStudy.getParticipants()).hasSize(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactlyInAnyOrder(host, applier),
                    () -> assertThat(findStudy.getApproveParticipants()).hasSize(2),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactlyInAnyOrder(host, applier)
            );

            int count = (int) events.stream(StudyApprovedEvent.class).count();
            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절")
    class reject {
        private Member host;
        private Member applier;
        private Study study;
        private static final String REASON = "나이가 너무 많아요";

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            applier = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 거절을 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() {
            // given
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.reject(study.getId(), applier.getId(), host.getId(), REASON))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("참여 신청자가 아니면 참여 거절을 할 수 없다")
        void throwExceptionByMemberIsNotApplier() {
            assertThatThrownBy(() -> participationService.reject(study.getId(), applier.getId(), host.getId(), REASON))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }

        @Test
        @DisplayName("참여 거절에 성공한다")
        void success() {
            // given
            study.applyParticipation(applier);

            // when
            participationService.reject(study.getId(), applier.getId(), host.getId(), REASON);

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants()).hasSize(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactlyInAnyOrder(host, applier),
                    () -> assertThat(findStudy.getApproveParticipants()).hasSize(1),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactlyInAnyOrder(host)
            );

            int count = (int) events.stream(StudyRejectedEvent.class).count();
            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("스터디 참여 취소")
    class cancel {
        private Member host;
        private Member participant;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            participant = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 취소를 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() {
            // given
            study.applyParticipation(participant);
            study.approveParticipation(participant);
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.cancel(study.getId(), participant.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("스터디 팀장은 참여 취소를 할 수 없다")
        void throwExceptionByMemberIsHost() {
            assertThatThrownBy(() -> participationService.cancel(study.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("참여자가 아니면 참여 취소를 할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
            assertThatThrownBy(() -> participationService.cancel(study.getId(), participant.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("참여 취소에 성공한다")
        void success() {
            // given
            study.applyParticipation(participant);
            study.approveParticipation(participant);

            // when
            participationService.cancel(study.getId(), participant.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants()).hasSize(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactlyInAnyOrder(host, participant),
                    () -> assertThat(findStudy.getApproveParticipants()).hasSize(1),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactlyInAnyOrder(host)
            );
        }
    }

    @Nested
    @DisplayName("스터디 졸업")
    class graduate {
        private Member host;
        private Member participant;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            participant = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 졸업을 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() {
            // given
            study.applyParticipation(participant);
            study.approveParticipation(participant);
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.graduate(study.getId(), participant.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("스터디 팀장은 졸업을 할 수 없다")
        void throwExceptionByMemberIsHost() {
            assertThatThrownBy(() -> participationService.graduate(study.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("참여자가 아니면 졸업을 할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
            assertThatThrownBy(() -> participationService.graduate(study.getId(), participant.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("졸업에 성공한다")
        void success() {
            // given
            study.applyParticipation(participant);
            study.approveParticipation(participant);

            // when
            participationService.graduate(study.getId(), participant.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getParticipants()).hasSize(2),
                    () -> assertThat(findStudy.getParticipants()).containsExactlyInAnyOrder(host, participant),
                    () -> assertThat(findStudy.getApproveParticipants()).hasSize(1),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactlyInAnyOrder(host),
                    () -> assertThat(findStudy.getGraduatedParticipants()).hasSize(1),
                    () -> assertThat(findStudy.getGraduatedParticipants()).containsExactlyInAnyOrder(participant)
            );

            int count = (int) events.stream(StudyGraduatedEvent.class).count();
            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("스터디 팀장 권한 위임")
    class delegateAuthority {
        private Member host;
        private Member participant;
        private Study study;

        @BeforeEach
        void setUp() {
            host = memberRepository.save(JIWON.toMember());
            participant = memberRepository.save(GHOST.toMember());
            study = studyRepository.save(SPRING.toOnlineStudy(host));
        }

        @Test
        @DisplayName("스터디가 종료되었다면 팀장 권한을 위임할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() {
            // given
            study.applyParticipation(participant);
            study.approveParticipation(participant);
            study.close();

            // when - then
            assertThatThrownBy(() -> participationService.delegateAuthority(study.getId(), participant.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("참여자가 아니면 팀장 권한을 위임할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
            assertThatThrownBy(() -> participationService.delegateAuthority(study.getId(), participant.getId(), host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("팀장 권한 위임에 성공한다")
        void success() {
            // given
            study.applyParticipation(participant);
            study.approveParticipation(participant);

            assertAll(
                    () -> assertThat(study.getHost()).isEqualTo(host),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(host, participant)
            );

            // when
            participationService.delegateAuthority(study.getId(), participant.getId(), host.getId());

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getHost()).isEqualTo(participant),
                    () -> assertThat(findStudy.getApproveParticipants()).containsExactlyInAnyOrder(host, participant)
            );
        }
    }
}
