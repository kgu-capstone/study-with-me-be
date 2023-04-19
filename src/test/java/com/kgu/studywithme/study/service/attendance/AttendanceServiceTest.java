package com.kgu.studywithme.study.service.attendance;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.LATE;
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

    private static final String STATUS = LATE.getDescription();
    private static final Integer WEEK = 1;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        anonymous = memberRepository.save(DUMMY1.toMember());

        study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member);
        study.approveParticipation(member);

        attendanceRepository.save(Attendance.recordAttendance(study, host, 1, AttendanceStatus.NON_ATTENDANCE));
        attendanceRepository.save(Attendance.recordAttendance(study, member, 1, AttendanceStatus.NON_ATTENDANCE));
    }

    @Nested
    @DisplayName("수동 출석 체크")
    class manualCheckAttendance {
        @Test
        @DisplayName("스터디 참여자가 아니면 출석 정보 Instance가 존재하지 않고 그에 따라서 출석 체크를 할 수 없다")
        void attendanceNotFound() {
            assertThatThrownBy(() -> attendanceService.manualCheckAttendance(study.getId(), anonymous.getId(), WEEK, STATUS))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ATTENDANCE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("수동 출석 체크에 성공한다")
        void success() {
            // when
            attendanceService.manualCheckAttendance(study.getId(), host.getId(), WEEK, STATUS);
            attendanceService.manualCheckAttendance(study.getId(), member.getId(), WEEK, STATUS);

            // then
            Attendance findohostAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK)
                    .orElseThrow();
            Attendance findMemberAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), member.getId(), WEEK)
                    .orElseThrow();

            assertAll(
                    () -> assertThat(findohostAttendance.getParticipant()).isEqualTo(host),
                    () -> assertThat(findohostAttendance.getWeek()).isEqualTo(WEEK),
                    () -> assertThat(findohostAttendance.getStatus().getDescription()).isEqualTo(STATUS),

                    () -> assertThat(findMemberAttendance.getParticipant()).isEqualTo(member),
                    () -> assertThat(findMemberAttendance.getWeek()).isEqualTo(WEEK),
                    () -> assertThat(findMemberAttendance.getStatus().getDescription()).isEqualTo(STATUS)
            );
        }
    }
}
