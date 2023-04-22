package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Participant 도메인 {Participants VO} 테스트")
class ParticipantsTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Capacity CAPACITY = Capacity.from(5);

    @Test
    @DisplayName("Participants를 생성한다")
    void construct() {
        Participants participants = Participants.of(HOST, CAPACITY);

        assertAll(
                () -> assertThat(participants.getHost()).isEqualTo(HOST),
                () -> assertThat(participants.getParticipants()).containsExactlyInAnyOrder(HOST),
                () -> assertThat(participants.getCapacity()).isEqualTo(CAPACITY)
        );
    }

    @Nested
    @DisplayName("스터디 팀장 권한 위임")
    class delegateStudyHostAuthority {
        @Test
        @DisplayName("스터디 참여자가 아닌 사용자에게는 팀장 권한을 위임할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);

            // when - then
            final Member anonymous = ANONYMOUS.toMember();
            assertThatThrownBy(() -> participants.delegateStudyHostAuthority(STUDY, anonymous))
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

            assertAll(
                    () -> assertThat(participants.getHost()).isEqualTo(HOST),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT)
            );

            // when
            participants.delegateStudyHostAuthority(STUDY, PARTICIPANT);

            // then
            assertAll(
                    () -> assertThat(participants.getHost()).isEqualTo(PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 신청")
    class apply {
        @Test
        @DisplayName("스터디 팀장은 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsHost() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.apply(STUDY, HOST))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("이미 참여 신청을 했거나 참여중인 사용자는 참여 신청을 다시 할 수 없다")
        void throwExceptionByMemberIsParticipant() {
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);

            assertThatThrownBy(() -> participants.apply(STUDY, PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("해당 스터디를 졸업했다면 다시 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsAlreadyGraduate() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);
            participants.graduate(PARTICIPANT);

            // when - then
            assertThatThrownBy(() -> participants.apply(STUDY, PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_ALREADY_GRADUATE_OR_CANCEL.getMessage());
        }

        @Test
        @DisplayName("해당 스터디에 대해서 이전에 참여 취소를 했다면 다시 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsAlreadyCancel() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);
            participants.cancel(PARTICIPANT);

            // when - then
            assertThatThrownBy(() -> participants.apply(STUDY, PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_ALREADY_GRADUATE_OR_CANCEL.getMessage());
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
                    () -> assertThat(participants.getParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApplier()).containsExactlyInAnyOrder(PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 승인")
    class approve {
        @Test
        @DisplayName("참여 신청 상태가 아닌 사용자는 참여 승인을 할 수 없다")
        void throwExceptionByMemberIsNotApplier() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.approve(PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_APPLIER.getMessage());
        }

        @Test
        @DisplayName("스터디 모집 인원이 꽉 찼을 경우 참여 승인을 할 수 없다")
        void throwExceptionByStudyCapacityIsFull() {
            // given
            Participants participants = Participants.of(HOST, Capacity.from(2));
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);

            // when - then
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
                    () -> assertThat(participants.getParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절")
    class reject {
        @Test
        @DisplayName("참여 신청 상태가 아닌 사용자는 참여 거절을 할 수 없다")
        void throwExceptionByMemberIsNotApplier() {
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
                    () -> assertThat(participants.getParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 취소")
    class cancel {
        @Test
        @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않는다면 참여 취소를 할 수 없다")
        void throwExceptionByMemberIsHost() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.cancel(HOST))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 참여 취소를 할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
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
                    () -> assertThat(participants.getParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 졸업")
    class graduate {
        @Test
        @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않는다면 졸업을 할 수 없다")
        void throwExceptionByMemberIsHost() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.graduate(HOST))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_HOST.getMessage());
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 졸업을 할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
            Participants participants = Participants.of(HOST, CAPACITY);

            assertThatThrownBy(() -> participants.graduate(PARTICIPANT))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("졸업에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);

            // when
            participants.graduate(PARTICIPANT);

            // then
            assertAll(
                    () -> assertThat(participants.getParticipants()).containsExactlyInAnyOrder(HOST, PARTICIPANT),
                    () -> assertThat(participants.getApproveParticipants()).containsExactlyInAnyOrder(HOST),
                    () -> assertThat(participants.getGraduatedParticipants()).containsExactlyInAnyOrder(PARTICIPANT)
            );
        }
    }

    @Nested
    @DisplayName("스터디 최대인원 수정")
    class updateCapacity {
        @Test
        @DisplayName("현재 참여인원보다 작은 값으로 Capacity를 수정할 수 없다")
        void throwExceptionByCapacityCannotBeLessThanParticipants() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);
            participants.apply(STUDY, PARTICIPANT);
            participants.approve(PARTICIPANT);

            // when - then
            assertThatThrownBy(() -> participants.updateCapacity(1))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_PARTICIPANTS.getMessage());
        }

        @Test
        @DisplayName("Capacity 수정에 성공한다")
        void success() {
            // given
            Participants participants = Participants.of(HOST, CAPACITY);

            // when
            participants.updateCapacity(2);

            // then
            assertThat(participants.getCapacity().getValue()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("스터디 참여자 나이 목록과 평균 나이를 계산한다")
    void getParticipantsAgesAndAverageAge() {
        /* 참여자 -> HOST */
        Participants participants = Participants.of(HOST, CAPACITY);
        participants.apply(STUDY, PARTICIPANT);

        List<Integer> ages1 = participants.getParticipantsAges();
        double averageAge1 = participants.getParticipantsAverageAge();
        assertAll(
                () -> assertThat(ages1).containsExactlyInAnyOrder(getAge(HOST.getBirth())),
                () -> assertThat(averageAge1).isEqualTo(getAge(HOST.getBirth()))
        );

        /* 참여자 -> HOST & PARTICIPANT */
        participants.approve(PARTICIPANT);

        List<Integer> ages2 = participants.getParticipantsAges();
        double averageAge2 = participants.getParticipantsAverageAge();
        assertAll(
                () -> assertThat(ages2).containsExactlyInAnyOrder(getAge(HOST.getBirth()), getAge(PARTICIPANT.getBirth())),
                () -> assertThat(averageAge2).isEqualTo((double) (getAge(HOST.getBirth()) + getAge(PARTICIPANT.getBirth())) / 2)
        );
    }

    private int getAge(LocalDate birth) {
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
