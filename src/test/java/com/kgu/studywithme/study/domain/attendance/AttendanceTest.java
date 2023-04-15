package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Attendance 도메인 테스트")
class AttendanceTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);

    @Test
    @DisplayName("Attendance를 생성한다")
    void construct() {
        Attendance attendanceHost = Attendance.recordAttendance(STUDY, HOST, 1, ATTENDANCE);
        Attendance attendanceParticipant = Attendance.recordAttendance(STUDY, PARTICIPANT, 1, ABSENCE);

        assertAll(
                () -> assertThat(attendanceHost.getStudy()).isEqualTo(STUDY),
                () -> assertThat(attendanceHost.getParticipant()).isEqualTo(HOST),
                () -> assertThat(attendanceHost.getWeek()).isEqualTo(1),
                () -> assertThat(attendanceHost.getStatus()).isEqualTo(ATTENDANCE),

                () -> assertThat(attendanceParticipant.getStudy()).isEqualTo(STUDY),
                () -> assertThat(attendanceParticipant.getParticipant()).isEqualTo(PARTICIPANT),
                () -> assertThat(attendanceParticipant.getWeek()).isEqualTo(1),
                () -> assertThat(attendanceParticipant.getStatus()).isEqualTo(ABSENCE)
        );
    }

    @Test
    @DisplayName("Attendance의 status를 변경한다")
    void updateAttendanceStatus() {
        // given
        Attendance attendance = Attendance.recordAttendance(STUDY, HOST, 1, ATTENDANCE);

        // when
        attendance.updateAttendanceStatus(LATE.getDescription());

        // then
        assertThat(attendance.getStatus()).isEqualTo(LATE);
    }
}
