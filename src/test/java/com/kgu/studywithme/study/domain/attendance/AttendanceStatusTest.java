package com.kgu.studywithme.study.domain.attendance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Attendance 도메인 {AttendanceStatus Enum} 테스트")
class AttendanceStatusTest {
    @Test
    @DisplayName("Description으로 AttendanceStatus를 조회한다")
    void fromDescription() {
        assertAll(
                () -> assertThat(AttendanceStatus.fromDescription("출석")).isEqualTo(ATTENDANCE),
                () -> assertThat(AttendanceStatus.fromDescription("지각")).isEqualTo(LATE),
                () -> assertThat(AttendanceStatus.fromDescription("결석")).isEqualTo(ABSENCE),
                () -> assertThat(AttendanceStatus.fromDescription("미출결")).isEqualTo(NON_ATTENDANCE),
                () -> assertThat(AttendanceStatus.fromDescription("????")).isEqualTo(NON_ATTENDANCE)
        );
    }

    @Test
    @DisplayName("AttendanceStatus의 Description 목록을 가져온다")
    void getAttendanceDescriptions() {
        List<AttendanceStatus> result = AttendanceStatus.getAttendanceStatuses();
        assertThat(result).containsExactlyInAnyOrder(ATTENDANCE, LATE, ABSENCE, NON_ATTENDANCE);
    }
}
