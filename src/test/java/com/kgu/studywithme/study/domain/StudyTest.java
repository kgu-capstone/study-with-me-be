package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
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
                () -> assertThat(onlineStudy.getLocation()).isNull(),
                () -> assertThat(onlineStudy.getMaxMembers()).isEqualTo(SPRING.getCapacity()),
                () -> assertThat(onlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(SPRING.getMinimumAttendanceForGraduation()),
                () -> assertThat(onlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(SPRING.getHashtags()),
                () -> assertThat(onlineStudy.getHost()).isEqualTo(HOST),
                () -> assertThat(onlineStudy.getParticipants()).containsExactlyInAnyOrder(HOST),
                () -> assertThat(onlineStudy.getApproveParticipants()).containsExactlyInAnyOrder(HOST),

                () -> assertThat(offlineStudy.getNameValue()).isEqualTo(TOSS_INTERVIEW.getName()),
                () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(TOSS_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(TOSS_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(TOSS_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getLocation()).isEqualTo(TOSS_INTERVIEW.getLocation()),
                () -> assertThat(offlineStudy.getMaxMembers()).isEqualTo(TOSS_INTERVIEW.getCapacity()),
                () -> assertThat(offlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(TOSS_INTERVIEW.getMinimumAttendanceForGraduation()),
                () -> assertThat(offlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(TOSS_INTERVIEW.getHashtags()),
                () -> assertThat(offlineStudy.getHost()).isEqualTo(HOST),
                () -> assertThat(offlineStudy.getParticipants()).containsExactlyInAnyOrder(HOST),
                () -> assertThat(offlineStudy.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
        );
    }

    @Test
    @DisplayName("Study 정보를 수정한다")
    void update() {
        Study onlineStudy = JAPANESE.toOnlineStudy(HOST);
        Study offlineStudy = TOSS_INTERVIEW.toOnlineStudy(HOST);

        onlineStudy.update(
                StudyName.from(CHINESE.name()),
                Description.from(CHINESE.getDescription()),
                CHINESE.getCapacity(),
                CHINESE.getCategory(),
                CHINESE.getType(),
                null, null,
                IN_PROGRESS,
                CHINESE.getHashtags()
        );

        offlineStudy.update(
                StudyName.from(KAKAO_INTERVIEW.name()),
                Description.from(KAKAO_INTERVIEW.getDescription()),
                KAKAO_INTERVIEW.getCapacity(),
                KAKAO_INTERVIEW.getCategory(),
                KAKAO_INTERVIEW.getType(),
                KAKAO_INTERVIEW.getLocation().getProvince(), KAKAO_INTERVIEW.getLocation().getCity(),
                IN_PROGRESS,
                KAKAO_INTERVIEW.getHashtags()
        );

        assertAll(
                () -> assertThat(onlineStudy.getNameValue()).isEqualTo(CHINESE.name()),
                () -> assertThat(onlineStudy.getDescriptionValue()).isEqualTo(CHINESE.getDescription()),
                () -> assertThat(onlineStudy.getCategory()).isEqualTo(CHINESE.getCategory()),
                () -> assertThat(onlineStudy.getType()).isEqualTo(CHINESE.getType()),
                () -> assertThat(onlineStudy.getLocation()).isNull(),
                () -> assertThat(onlineStudy.getMaxMembers()).isEqualTo(CHINESE.getCapacity()),
                () -> assertThat(onlineStudy.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(onlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(JAPANESE.getMinimumAttendanceForGraduation()),
                () -> assertThat(onlineStudy.getHashtags()).hasSize(CHINESE.getHashtags().size()),
                () -> assertThat(onlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(CHINESE.getHashtags()),
                () -> assertThat(onlineStudy.getHost()).isEqualTo(HOST),
                () -> assertThat(onlineStudy.getParticipants()).containsExactlyInAnyOrder(HOST),

                () -> assertThat(offlineStudy.getNameValue()).isEqualTo(KAKAO_INTERVIEW.name()),
                () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(KAKAO_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(KAKAO_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(KAKAO_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getLocation().getProvince()).isEqualTo(KAKAO_INTERVIEW.getLocation().getProvince()),
                () -> assertThat(offlineStudy.getLocation().getCity()).isEqualTo(KAKAO_INTERVIEW.getLocation().getCity()),
                () -> assertThat(offlineStudy.getMaxMembers()).isEqualTo(KAKAO_INTERVIEW.getCapacity()),
                () -> assertThat(offlineStudy.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(offlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(TOSS_INTERVIEW.getMinimumAttendanceForGraduation()),
                () -> assertThat(offlineStudy.getHashtags()).hasSize(KAKAO_INTERVIEW.getHashtags().size()),
                () -> assertThat(offlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(KAKAO_INTERVIEW.getHashtags()),
                () -> assertThat(offlineStudy.getHost()).isEqualTo(HOST),
                () -> assertThat(offlineStudy.getParticipants()).containsExactlyInAnyOrder(HOST)
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
        void throwExceptionByStudyIsAlreadyClosed() {
            study.close();

            assertThatThrownBy(() -> study.applyParticipation(participant))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_CLOSED.getMessage());
        }

        @Test
        @DisplayName("스터디 모집이 완료됐다면 참여 신청을 할 수 없다")
        void throwExceptionByRecruitmentIsComplete() {
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
                    () -> assertThat(study.getParticipants()).containsExactlyInAnyOrder(HOST, participant),
                    () -> assertThat(study.getApplier()).containsExactlyInAnyOrder(participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
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
        void throwExceptionByStudyIsAlreadyClosed() {
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
                    () -> assertThat(study.getParticipants()).containsExactlyInAnyOrder(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(HOST, participant)
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
        void throwExceptionByStudyIsAlreadyClosed() {
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
                    () -> assertThat(study.getParticipants()).containsExactlyInAnyOrder(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
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
        void throwExceptionByStudyIsAlreadyClosed() {
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
                    () -> assertThat(study.getParticipants()).containsExactlyInAnyOrder(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(HOST)
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
        void throwExceptionByStudyIsAlreadyClosed() {
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
                    () -> assertThat(study.getParticipants()).containsExactlyInAnyOrder(HOST, participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(HOST),
                    () -> assertThat(study.getGraduatedParticipants()).containsExactlyInAnyOrder(participant)
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
        void throwExceptionByStudyIsAlreadyClosed() {
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
            assertAll(
                    () -> assertThat(study.getHost()).isEqualTo(participant),
                    () -> assertThat(study.getApproveParticipants()).containsExactlyInAnyOrder(participant, HOST)
            );
        }
    }

    @Test
    @DisplayName("스터디에 대한 공지사항을 작성한다")
    void addNotice() {
        // given
        Study study = SPRING.toOnlineStudy(HOST);

        // when
        study.addNotice("Notice 1", "공지사항1 입니다.");
        study.addNotice("Notice 2", "공지사항2 입니다.");
        study.addNotice("Notice 3", "공지사항3 입니다.");

        // then
        assertAll(
                () -> assertThat(study.getNotices()).hasSize(3),
                () -> assertThat(study.getNotices())
                        .map(Notice::getTitle)
                        .containsExactlyInAnyOrder("Notice 1", "Notice 2", "Notice 3"),
                () -> assertThat(study.getNotices())
                        .map(Notice::getContent)
                        .containsExactlyInAnyOrder("공지사항1 입니다.", "공지사항2 입니다.", "공지사항3 입니다.")
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
        void throwExceptionByMemberIsNotParticipant() {
            assertThatThrownBy(() -> study.recordAttendance(participant, 1, ATTENDANCE))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("참여자들에 대한 출석 체크에 성공한다")
        void success() {
            // given
            study.approveParticipation(participant);

            // when
            study.recordAttendance(HOST, 1, ATTENDANCE);
            study.recordAttendance(participant, 1, LATE);

            // then
            assertAll(
                    () -> assertThat(study.getAttendances()).hasSize(2),
                    () -> assertThat(study.getAttendances())
                            .map(Attendance::getStatus)
                            .containsExactlyInAnyOrder(ATTENDANCE, LATE),
                    () -> assertThat(study.getAttendances())
                            .map(Attendance::getStudy)
                            .containsExactlyInAnyOrder(study, study),
                    () -> assertThat(study.getAttendances())
                            .map(Attendance::getParticipant)
                            .containsExactlyInAnyOrder(HOST, participant)
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
    @DisplayName("스터디 주차 등록 [과제 X]")
    class createWeek {
        private Study study;
        private Week week1;
        private Week week2;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            week1 = STUDY_WEEKLY_5.toWeek(study);
            week2 = STUDY_WEEKLY_6.toWeek(study);
        }

        @Test
        @DisplayName("이미 해당 주차가 등록되었다면 중복으로 등록할 수 없다")
        void throwExceptionByAlreadyWeekCreated() {
            // given
            study.createWeek(week1.getTitle(), week1.getContent(), week1.getWeek(), week1.getPeriod(), STUDY_WEEKLY_1.getAttachments());

            // when - then
            assertThatThrownBy(() -> study.createWeek(week2.getTitle(), week2.getContent(), week1.getWeek(), week2.getPeriod(), STUDY_WEEKLY_2.getAttachments()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_WEEK_CREATED.getMessage());
        }

        @Test
        @DisplayName("주차 등록에 성공한다")
        void success() {
            // given
            study.createWeek(week1.getTitle(), week1.getContent(), week1.getWeek(), week1.getPeriod(), STUDY_WEEKLY_1.getAttachments());

            // when
            study.createWeek(week2.getTitle(), week2.getContent(), week2.getWeek(), week2.getPeriod(), STUDY_WEEKLY_2.getAttachments());

            // then
            assertThat(study.getWeeks()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("스터디 주차 등록 [과제 O]")
    class createWeekWithAssignment {
        private Study study;
        private Week week1;
        private Week week2;

        @BeforeEach
        void setUp() {
            study = SPRING.toOnlineStudy(HOST);
            week1 = STUDY_WEEKLY_1.toWeekWithAssignment(study);
            week2 = STUDY_WEEKLY_2.toWeekWithAssignment(study);
        }

        @Test
        @DisplayName("이미 해당 주차가 등록되었다면 중복으로 등록할 수 없다")
        void throwExceptionByAlreadyWeekCreated() {
            // given
            study.createWeekWithAssignment(
                    week1.getTitle(), week1.getContent(), week1.getWeek(), week1.getPeriod(),
                    true, STUDY_WEEKLY_1.getAttachments()
            );

            // when - then
            assertThatThrownBy(() -> study.createWeekWithAssignment(
                    week2.getTitle(), week2.getContent(), week1.getWeek(), week2.getPeriod(),
                    true, STUDY_WEEKLY_2.getAttachments()
            ))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ALREADY_WEEK_CREATED.getMessage());
        }

        @Test
        @DisplayName("주차 등록에 성공한다")
        void success() {
            // given
            study.createWeekWithAssignment(
                    week1.getTitle(), week1.getContent(), week1.getWeek(), week1.getPeriod(),
                    true, STUDY_WEEKLY_1.getAttachments()
            );

            // when
            study.createWeekWithAssignment(
                    week2.getTitle(), week2.getContent(), week2.getWeek(), week2.getPeriod(),
                    true, STUDY_WEEKLY_2.getAttachments()
            );

            // then
            assertThat(study.getWeeks()).hasSize(2);
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
        void throwExceptionByMemberIsNotGraduated() {
            assertThatThrownBy(() -> study.writeReview(participant, "리뷰입니다."))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_GRADUATED.getMessage());
        }

        @Test
        @DisplayName("이미 리뷰를 작성했다면 중복으로 작성할 수 없다")
        void throwExceptionByAlreadyReviewWritten() {
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

    @Test
    @DisplayName("스터디 참여자 나이 목록과 평균 나이를 계산한다")
    void getParticipantsAgesAndAverageAge() {
        Study study = SPRING.toOnlineStudy(HOST);
        Member participant = GHOST.toMember();

        /* 참여자 -> HOST */
        List<Integer> ages1 = study.getParticipantsAges();
        double averageAge1 = study.getParticipantsAverageAge();
        assertAll(
                () -> assertThat(ages1).containsExactlyInAnyOrder(getAge(HOST.getBirth())),
                () -> assertThat(averageAge1).isEqualTo(getAge(HOST.getBirth()))
        );

        /* 참여자 -> HOST & PARTICIPANT */
        study.applyParticipation(participant);
        study.approveParticipation(participant);

        List<Integer> ages2 = study.getParticipantsAges();
        double averageAge2 = study.getParticipantsAverageAge();
        assertAll(
                () -> assertThat(ages2).containsExactlyInAnyOrder(getAge(HOST.getBirth()), getAge(participant.getBirth())),
                () -> assertThat(averageAge2).isEqualTo((double) (getAge(HOST.getBirth()) + getAge(participant.getBirth())) / 2)
        );
    }

    private int getAge(LocalDate birth) {
        return Period.between(birth, LocalDate.now()).getYears();
    }

    @Test
    @DisplayName("졸업 요건[최소 출석 횟수]을 만족했는지 여부를 확인한다")
    void isGraduationRequirementsFulfilled() {
        // given
        Study study = SPRING.toOnlineStudy(HOST);
        final int notEnough = study.getMinimumAttendanceForGraduation() - 1;
        final int enough1 = study.getMinimumAttendanceForGraduation();
        final int enough2 = study.getMinimumAttendanceForGraduation() + 1;

        // when
        boolean actual1 = study.isGraduationRequirementsFulfilled(notEnough);
        boolean actual2 = study.isGraduationRequirementsFulfilled(enough1);
        boolean actual3 = study.isGraduationRequirementsFulfilled(enough2);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }
}
