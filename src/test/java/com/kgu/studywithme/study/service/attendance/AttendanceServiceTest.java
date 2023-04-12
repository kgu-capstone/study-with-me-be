package com.kgu.studywithme.study.service.attendance;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
import com.kgu.studywithme.study.controller.utils.AttendanceRequestUtils;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> AttendanceService 테스트")
class AttendanceServiceTest extends ServiceTest {
    @Autowired
    private AttendanceService attendanceService;

    private Member host;
    private Member member;
    private Study study;
    private AttendanceRequest request;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member);
        study.approveParticipation(member);

        request = AttendanceRequestUtils.createAttendanceRequest();

        attendanceRepository.save(Attendance.recordAttendance(1, AttendanceStatus.NON_ATTENDANCE, study, host));
        attendanceRepository.save(Attendance.recordAttendance(1, AttendanceStatus.NON_ATTENDANCE, study, member));
    }

    @Nested
    @DisplayName("수동 출석 체크")
    class manualCheckAttendance {
        @Test
        @DisplayName("팀장이 아니라면 수동으로 출석 정보를 변경할 수 없다")
        void memberIsNotHost() {
            assertThatThrownBy(() -> attendanceService.manualCheckAttendance(study.getId(), member.getId(), member.getId(), request.status(), request.week()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_HOST.getMessage());
        }

        @Test
        @DisplayName("해당 사용자의 출석 정보가 존재하지 않는다")
        void attendanceNotFound() {
            assertThatThrownBy(() -> attendanceService.manualCheckAttendance(study.getId(), member.getId(), host.getId(), request.status(), 2))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.ATTENDANCE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("수동 출석 체크에 성공한다")
        void success() {
            attendanceService.manualCheckAttendance(study.getId(), member.getId(), host.getId(), request.status(), request.week());

            Attendance findAttendance = attendanceRepository.findByStudyAndParticipantAndWeek(study, member, request.week())
                    .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND));

            assertAll(
                    () -> assertThat(findAttendance.getStatus().getDescription()).isEqualTo(request.status()),
                    () -> assertThat(findAttendance.getWeek()).isEqualTo(request.week().intValue())
            );
        }
    }
}