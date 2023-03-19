package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Participant 도메인 {Participants VO} 테스트")
class ParticipantsTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toStudy(HOST);
    private static final Capacity CAPACITY = Capacity.from(5);

    @Test
    @DisplayName("Participants를 생성한다")
    void construct() {
        Participants participants = Participants.of(HOST, CAPACITY);

        assertAll(
                () -> assertThat(participants.getHost()).isEqualTo(HOST),
                () -> assertThat(participants.getParticipants()).containsExactly(HOST),
                () -> assertThat(participants.getCapacity()).isEqualTo(CAPACITY)
        );
    }

    @Nested
    @DisplayName("스터디 팀장 권한 위임")
    class delegateStudyHostAuthority {
        @Test
        @DisplayName("스터디 참여자가 아닌 사용자에게는 팀장 권한을 위임할 수 없다")
        void failureByAnonymousMember() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);

            // when - then
            final Member anonymous = ANONYMOUS.toMember();
            assertThatThrownBy(() -> participants.delegateStudyHostAuthority(anonymous))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }
        
        @Test
        @DisplayName("팀장 권한 위임에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);

            // when
            participants.delegateStudyHostAuthority(PARTICIPANT);

            // then
            assertThat(participants.getHost()).isEqualTo(PARTICIPANT);
        }
    }

    @Nested
    @DisplayName("스터디 참여 신청")
    class apply {
        @Test
        @DisplayName("스터디 팀장은 참여 신청을 할 수 없다")
        void failureByHost() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.apply(STUDY, HOST))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("이미 참여 신청을 했거나 참여중인 사용자는 참여 신청을 다시 할 수 없다")
        void failureByAlreadyApply() {
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);

            assertThatThrownBy(() -> participants.apply(STUDY, PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("참여 신청에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);

            // when
            participants.apply(STUDY, PARTICIPANT);

            // then
            assertAll(
                    () -> assertThat(participants.getParticipants()).containsExactly(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactly(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 승인")
    class approve {
        @Test
        @DisplayName("참여 신청 상태가 아닌 사용자는 참여 승인을 할 수 없다")
        void failureByNotApplier() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.approve(PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }

        @Test
        @DisplayName("스터디 모집 인원이 꽉 찼을 경우 참여 승인을 할 수 없다")
        void failureByAlreadyCapacityFull() {
            Participants participants = Participants.of(HOST, Capacity.from(2));
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);

            final Member other = ANONYMOUS.toMember();
            participants.apply(STUDY, other);
            assertThatThrownBy(() -> participants.approve(other))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.STUDY_CAPACITY_IS_FULL.getMessage());
        }

        @Test
        @DisplayName("참여 승인에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);

            // when
            participants.approve(PARTICIPANT);

            // then
            assertAll(
                    () -> assertThat(participants.getParticipants()).containsExactly(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactly(HOST, PARTICIPANT)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절")
    class reject {
        @Test
        @DisplayName("참여 신청 상태가 아닌 사용자는 참여 거절을 할 수 없다")
        void failureByNotApplier() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.reject(PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }

        @Test
        @DisplayName("참여 거절에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);

            // when
            participants.reject(PARTICIPANT);

            // then
            assertAll(
                    () -> assertThat(participants.getParticipants()).containsExactly(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactly(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 취소")
    class cancel {
        @Test
        @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않는다면 참여 취소를 할 수 없다")
        void failureByHost() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.cancel(HOST))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 참여 취소를 할 수 없다")
        void failureByNotParticipant() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.cancel(PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("참여 취소에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);

            // when
            participants.cancel(PARTICIPANT);

            // then
            assertAll(
                    () -> assertThat(participants.getParticipants()).containsExactly(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactly(HOST)
            );
        }
    }
}
