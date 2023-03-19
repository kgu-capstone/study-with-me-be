package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Attendance 도메인 테스트")
class AttendanceTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toStudy(HOST);

    @Test
    @DisplayName("Attendance를 생성한다")
    void construct() {
        Attendance attendanceHost = Attendance.recordAttendance(1, ATTENDANCE, STUDY, HOST);
        Attendance attendanceParticipant = Attendance.recordAttendance(1, ABSENCE, STUDY, PARTICIPANT);

        assertAll(
                () -> assertThat(attendanceHost.getWeek()).isEqualTo(1),
                () -> assertThat(attendanceHost.getStatus()).isEqualTo(ATTENDANCE),
                () -> assertThat(attendanceHost.getStudy()).isEqualTo(STUDY),
                () -> assertThat(attendanceHost.getParticipant()).isEqualTo(HOST),

                () -> assertThat(attendanceParticipant.getWeek()).isEqualTo(1),
                () -> assertThat(attendanceParticipant.getStatus()).isEqualTo(ABSENCE),
                () -> assertThat(attendanceParticipant.getStudy()).isEqualTo(STUDY),
                () -> assertThat(attendanceParticipant.getParticipant()).isEqualTo(PARTICIPANT)
        );
    }
}
