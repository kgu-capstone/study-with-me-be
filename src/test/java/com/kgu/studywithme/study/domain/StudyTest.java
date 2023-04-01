package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.assignment.Period;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.LATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study 도메인 테스트")
class StudyTest {
    private static final Member HOST = JIWON.toMember();

    @Test
    @DisplayName("Study를 생성한다")
    void construct() {
        Study onlineStudy = SPRING.toOnlineStudy(HOST);
        Study offlineStudy = TOSS_INTERVIEW.toOfflineStudy(HOST);

        assertAll(
                () -> assertThat(onlineStudy.getNameValue()).isEqualTo(SPRING.getName()),
                () -> assertThat(onlineStudy.getDescriptionValue()).isEqualTo(SPRING.getDescription()),
                () -> assertThat(onlineStudy.getCategory()).isEqualTo(SPRING.getCategory()),
                () -> assertThat(onlineStudy.getType()).isEqualTo(SPRING.getType()),
                () -> assertThat(onlineStudy.getArea()).isNull(),
                () -> assertThat(onlineStudy.getMaxMembers()).isEqualTo(SPRING.getCapacity()),
                () -> assertThat(onlineStudy.getHashtags()).containsAll(SPRING.getHashtags()),
                () -> assertThat(onlineStudy.getHost()).isEqualTo(HOST),
                () -> assertThat(onlineStudy.getParticipants()).containsExactly(HOST),
                () -> assertThat(onlineStudy.getApproveParticipants()).containsExactly(HOST),

                () -> assertThat(offlineStudy.getNameValue()).isEqualTo(TOSS_INTERVIEW.getName()),
                () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(TOSS_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(TOSS_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(TOSS_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getArea()).isEqualTo(TOSS_INTERVIEW.getArea()),
                () -> assertThat(offlineStudy.getMaxMembers()).isEqualTo(TOSS_INTERVIEW.getCapacity()),
                () -> assertThat(offlineStudy.getHashtags()).containsAll(TOSS_INTERVIEW.getHashtags()),
                () -> assertThat(offlineStudy.getHost()).isEqualTo(HOST),
                () -> assertThat(offlineStudy.getParticipants()).containsExactly(HOST),
                () -> assertThat(offlineStudy.getApproveParticipants()).containsExactly(HOST)
        );
    }

    @Test
    @DisplayName("스터디 모집 상태를 모집 완료로 변경한다")
    void completeRecruitment() {
        // given
        Study study = SPRING.toOnlineStudy(HOST);

        // when
        study.completeRecruitment();

        // then
        assertThat(study.isRecruitmentComplete()).isTrue();
    }

    @Test
    @DisplayName("스터디를 종료한다")
    void closeStudy() {
        // given
        Study study = SPRING.toOnlineStudy(HOST);

        // when
        study.close();

        // then
        assertThat(study.isClosed()).isTrue();
    }

    @Nested
    @DisplayName("스터디 참여 신청")
    class applyParticipation {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();
        }

        @Test
        @DisplayName("종료된 스터디에는 참여 신청을 할 수 없다")
        void failureByClosedStudy() {
            study.close();

            assertThatThrownBy(() -> study.applyParticipation(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("스터디 모집이 완료됐다면 참여 신청을 할 수 없다")
        void failureByCompletedRecruitment() {
            study.completeRecruitment();

            assertThatThrownBy(() -> study.applyParticipation(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.RECRUITMENT_IS_COMPLETE.getMessage());
        }

        @Test
        @DisplayName("참여 신청에 성공한다")
        void success() {
            // when
            study.applyParticipation(participant);

            // then
            assertAll(
                    () -> assertThat(study.getParticipants()).containsExactly(HOST, participant),
                    () -> assertThat(study.getApplier()).containsExactly(participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactly(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 승인")
    class approveParticipation {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
        }

        @Test
        @DisplayName("종료된 스터디에는 참여 승인을 할 수 없다")
        void failureByClosedStudy() {
            study.close();

            assertThatThrownBy(() -> study.approveParticipation(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("참여 승인에 성공한다")
        void success() {
            // when
            study.approveParticipation(participant);

            // then
            assertAll(
                    () -> assertThat(study.getParticipants()).containsExactly(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactly(HOST, participant)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절")
    class rejectParticipation {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
        }

        @Test
        @DisplayName("종료된 스터디에는 참여 거절을 할 수 없다")
        void failureByClosedStudy() {
            study.close();

            assertThatThrownBy(() -> study.rejectParticipation(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("참여 거절에 성공한다")
        void success() {
            // when
            study.rejectParticipation(participant);

            // then
            assertAll(
                    () -> assertThat(study.getParticipants()).containsExactly(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactly(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 참여 취소")
    class cancelParticipation {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
            study.approveParticipation(participant);
        }

        @Test
        @DisplayName("종료된 스터디에는 참여 취소를 할 수 없다")
        void failureByClosedStudy() {
            study.close();

            assertThatThrownBy(() -> study.cancelParticipation(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("참여 취소에 성공한다")
        void success() {
            // when
            study.cancelParticipation(participant);

            // then
            assertAll(
                    () -> assertThat(study.getParticipants()).containsExactly(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactly(HOST)
            );
        }
    }

    @Nested
    @DisplayName("스터디 졸업")
    class graduateParticipant {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
            study.approveParticipation(participant);
        }

        @Test
        @DisplayName("종료된 스터디에서는 졸업을 할 수 없다")
        void failureByClosedStudy() {
            study.close();

            assertThatThrownBy(() -> study.graduateParticipant(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("졸업에 성공한다")
        void success() {
            // when
            study.graduateParticipant(participant);

            // then
            assertAll(
                    () -> assertThat(study.getParticipants()).containsExactly(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactly(HOST),
                    () -> assertThat(study.getGraduatedParticipants()).containsExactly(participant)
            );
        }
    }

    @Nested
    @DisplayName("스터디 팀장 권한 위임")
    class delegateStudyHostAuthority {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
            study.approveParticipation(participant);
        }

        @Test
        @DisplayName("종료된 스터디에서는 팀장 권한을 위임할 수 없다")
        void failureByClosedStudy() {
            study.close();

            assertThatThrownBy(() -> study.delegateStudyHostAuthority(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("팀장 권한 위임에 성공한다")
        void success() {
            // when
            study.delegateStudyHostAuthority(participant);

            // then
            assertThat(study.getHost()).isEqualTo(participant);
        }
    }

    @Test
    @DisplayName("스터디에 대한 공지사항을 작성한다")
    void writeNotice() {
        // given
        Study study = SPRING.toOnlineStudy(HOST);

        // when
        study.addNotice("Notice 1", "공지사항1 입니다.");
        study.addNotice("Notice 2", "공지사항2 입니다.");
        study.addNotice("Notice 3", "공지사항3 입니다.");

        // then
        assertAll(
                () -> assertThat(study.getNotices().size()).isEqualTo(3),
                () -> assertThat(study.getNotices())
                        .map(Notice::getTitle)
                        .containsExactly("Notice 1", "Notice 2", "Notice 3"),
                () -> assertThat(study.getNotices())
                        .map(Notice::getContent)
                        .containsExactly("공지사항1 입니다.", "공지사항2 입니다.", "공지사항3 입니다.")
        );
    }

    @Nested
    @DisplayName("스터디 출석 체크")
    class recordAttendance {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
        }

        @Test
        @DisplayName("스터디에 참여하고 있지 않은 사용자에 대한 출석 체크는 불가능하다")
        void failureByAnonymousMember() {
            assertThatThrownBy(() -> study.recordAttendance(1, ATTENDANCE, participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("참여자들에 대한 출석 체크에 성공한다")
        void success() {
            // given
            study.approveParticipation(participant);

            // when
            study.recordAttendance(1, ATTENDANCE, HOST);
            study.recordAttendance(1, LATE, participant);

            // then
            assertAll(
                    () -> assertThat(study.getAttendances().size()).isEqualTo(2),
                    () -> assertThat(study.getAttendances())
                            .map(Attendance::getStatus)
                            .containsExactly(ATTENDANCE, LATE),
                    () -> assertThat(study.getAttendances())
                            .map(Attendance::getStudy)
                            .containsExactly(study, study),
                    () -> assertThat(study.getAttendances())
                            .map(Attendance::getParticipant)
                            .containsExactly(HOST, participant)
            );
        }
    }

    @Test
    @DisplayName("해당 사용자가 스터디 참여자인지 확인한다")
    void isParticipant() {
        // given
        final Study study = SPRING.toOnlineStudy(HOST);
        final Member ghost = GHOST.toMember();
        study.applyParticipation(ghost);

        // when - then
        assertThatThrownBy(() -> study.validateMemberIsParticipant(ghost))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        assertDoesNotThrow(() -> study.validateMemberIsParticipant(HOST));

        study.approveParticipation(ghost);
        assertDoesNotThrow(() -> study.validateMemberIsParticipant(ghost));
    }

    @Nested
    @DisplayName("스터디 과제 등록")
    class registerAssignment {
        private Study study;
        private Member participant;
        private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 과제를 등록할 수 없다")
        void failureByAnonymousMember() {
            assertThatThrownBy(() -> study.registerAssignment(1, PERIOD, participant, "과제 1", "첫번째 과제입니다."))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("이미 해당 주차에 등록된 과제가 있으면 중복으로 등록할 수 없다")
        void failureByAlreadyRegisterPerWeek() {
            // given
            study.approveParticipation(participant);
            study.registerAssignment(1, PERIOD, HOST, "과제 1", "첫번째 과제입니다.");

            // when - then
            assertThatThrownBy(() -> study.registerAssignment(1, PERIOD, participant, "과제 2", "두번째 과제입니다."))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_ASSIGNMENT_EXISTS_PER_WEEK.getMessage());
        }

        @Test
        @DisplayName("과제 등록에 성공한다")
        void success() {
            // given
            study.approveParticipation(participant);
            study.registerAssignment(1, PERIOD, HOST, "과제 1", "첫번째 과제입니다.");

            // when
            study.registerAssignment(2, PERIOD, participant, "과제 2", "두번째 과제입니다.");

            // then
            assertThat(study.getAssignments().getCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("스터디 리뷰 작성")
    class writeReview {
        private Study study;
        private Member participant;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            participant = GHOST.toMember();

            study.applyParticipation(participant);
            study.approveParticipation(participant);
        }

        @Test
        @DisplayName("스터디 졸업생이 아니면 리뷰를 작성할 수 없다")
        void failureByAnonymousMember() {
            assertThatThrownBy(() -> study.writeReview(participant, "리뷰입니다."))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_GRADUATED.getMessage());
        }

        @Test
        @DisplayName("이미 리뷰를 작성했다면 중복으로 작성할 수 없다")
        void failureByAlreadyRegisterPerWeek() {
            // given
            study.graduateParticipant(participant);
            study.writeReview(participant, "리뷰입니다.");

            // when - then
            assertThatThrownBy(() -> study.writeReview(participant, "리뷰입니다22."))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_REVIEW_WRITTEN.getMessage());
        }

        @Test
        @DisplayName("리뷰 작성에 성공한다")
        void success() {
            // given
            study.graduateParticipant(participant);

            // when
            study.writeReview(participant, "리뷰입니다.");

            // then
            assertThat(study.getReviews()).hasSize(1);
        }
    }
}
