package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> AttendanceRepository 테스트")
public class AttendanceRepositoryTest extends RepositoryTest {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));

        attendanceRepository.save(Attendance.recordAttendance(study, host, 1, NON_ATTENDANCE));
    }

    @Test
    @DisplayName("스터디, 참여자, 주차로 출석 정보를 조회한다")
    void findByStudyAndParticipantAndWeek() {
        Attendance findAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), 1).orElseThrow();

        assertAll(
                () -> assertThat(findAttendance.getStudy()).isEqualTo(study),
                () -> assertThat(findAttendance.getParticipant()).isEqualTo(host),
                () -> assertThat(findAttendance.getStatus()).isEqualTo(NON_ATTENDANCE),
                () -> assertThat(findAttendance.getWeek()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("참여자의 출석 상태를 업데이트한다")
    void applyParticipantAttendanceStatus() {
        /* 미출결 */
        Attendance nonAttendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), 1)
                .orElseThrow();
        assertThat(nonAttendance.getStatus()).isEqualTo(NON_ATTENDANCE);

        /* to 출석 */
        attendanceRepository.applyParticipantAttendanceStatus(host.getId(), 1, ATTENDANCE);
        Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), 1)
                .orElseThrow();
        assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE);

        /* to 지각 */
        attendanceRepository.applyParticipantAttendanceStatus(host.getId(), 1, LATE);
        Attendance late = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), 1)
                .orElseThrow();
        assertThat(late.getStatus()).isEqualTo(LATE);

        /* to 결석 */
        attendanceRepository.applyParticipantAttendanceStatus(host.getId(), 1, ABSENCE);
        Attendance absence = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), 1)
                .orElseThrow();
        assertThat(absence.getStatus()).isEqualTo(ABSENCE);
    }
}
