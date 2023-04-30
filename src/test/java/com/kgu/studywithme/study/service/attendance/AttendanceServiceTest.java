package com.kgu.studywithme.study.service.attendance;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> AttendanceService 테스트")
class AttendanceServiceTest extends ServiceTest {
    @Autowired
    private AttendanceService attendanceService;

    private Member host;
    private Member member;
    private Member anonymous;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        anonymous = memberRepository.save(DUMMY1.toMember());

        study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member);
        study.approveParticipation(member);

        attendanceRepository.save(Attendance.recordAttendance(study, host, 1, NON_ATTENDANCE));
        attendanceRepository.save(Attendance.recordAttendance(study, member, 1, NON_ATTENDANCE));
    }

    @Nested
    @DisplayName("수동 출석 체크")
    class manualCheckAttendance {
        @Test
        @DisplayName("스터디 참여자가 아니면 출석 정보 Instance가 존재하지 않고 그에 따라서 출석 체크를 할 수 없다")
        void throwExceptionByAttendanceNotFound() {
            assertThatThrownBy(() -> attendanceService.manualCheckAttendance(study.getId(), anonymous.getId(), 1, LATE))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ATTENDANCE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("미출석 상태로 출석 체크를 진행할 수 없다")
        void throwExceptionByCannotUpdateToNonAttendance() {
            assertThatThrownBy(() -> attendanceService.manualCheckAttendance(study.getId(), member.getId(), 1, NON_ATTENDANCE))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CANNOT_UPDATE_TO_NON_ATTENDANCE.getMessage());
        }

        @Test
        @DisplayName("수동 출석 체크에 성공한다 [이전 출석 = 미출결]")
        void successWithBeforeNonAttendance() {
            // when
            attendanceService.manualCheckAttendance(study.getId(), host.getId(), 1, LATE);
            attendanceService.manualCheckAttendance(study.getId(), member.getId(), 1, LATE);

            // then
            Attendance findHostAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), 1)
                    .orElseThrow();
            Attendance findMemberAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), member.getId(), 1)
                    .orElseThrow();

            assertAll(
                    () -> assertThat(findHostAttendance.getParticipant()).isEqualTo(host),
                    () -> assertThat(findHostAttendance.getWeek()).isEqualTo(1),
                    () -> assertThat(findHostAttendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(host.getScore()).isEqualTo(80 - 1),

                    () -> assertThat(findMemberAttendance.getParticipant()).isEqualTo(member),
                    () -> assertThat(findMemberAttendance.getWeek()).isEqualTo(1),
                    () -> assertThat(findMemberAttendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(member.getScore()).isEqualTo(80 - 1)
            );
        }

        @Test
        @DisplayName("수동 출석 체크에 성공한다 [이전 출석 = 결석]")
        void successWithBeforeAbsence() {
            // given
            member.applyScoreByAttendanceStatus(ABSENCE); // score = 75
            attendanceService.manualCheckAttendance(study.getId(), member.getId(), 1, ABSENCE); // score = 70

            // when
            attendanceService.manualCheckAttendance(study.getId(), member.getId(), 1, LATE);

            // then
            Attendance findMemberAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), member.getId(), 1)
                    .orElseThrow();

            assertAll(
                    () -> assertThat(findMemberAttendance.getParticipant()).isEqualTo(member),
                    () -> assertThat(findMemberAttendance.getWeek()).isEqualTo(1),
                    () -> assertThat(findMemberAttendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(member.getScore()).isEqualTo(70 + 5 - 1)
            );
        }
    }
}
