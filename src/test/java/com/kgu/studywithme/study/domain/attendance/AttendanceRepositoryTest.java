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

import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
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
    private final Member[] participants = new Member[7];
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        participants[0] = memberRepository.save(DUMMY1.toMember());
        participants[1] = memberRepository.save(DUMMY2.toMember());
        participants[2] = memberRepository.save(DUMMY3.toMember());
        participants[3] = memberRepository.save(DUMMY4.toMember());
        participants[4] = memberRepository.save(DUMMY5.toMember());
        participants[5] = memberRepository.save(DUMMY6.toMember());
        participants[6] = memberRepository.save(DUMMY7.toMember());

        study = studyRepository.save(SPRING.toOnlineStudy(host));
        beParticipation();

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
    @DisplayName("해당 스터디의 해당 주차의 참여자들의 상태를 수정한다 [Bulk Update]")
    void updateParticipantStatus() {
        // given
        applyNonAttendanceToAllParticipants();

        // when: [0, 1, 2, 3] to ATTENDANCE
        attendanceRepository.updateParticipantStatus(
                study.getId(),
                1,
                Set.of(participants[0].getId(), participants[1].getId(), participants[2].getId(), participants[3].getId()),
                ATTENDANCE
        );

        // then
        List<Attendance> attendances = attendanceRepository.findAll();
        assertAll(
                () -> assertThat(attendances).hasSize(8),
                () -> assertThat(attendances)
                        .map(Attendance::getParticipant)
                        .map(Member::getId)
                        .containsExactly(
                                host.getId(),
                                participants[0].getId(),
                                participants[1].getId(),
                                participants[2].getId(),
                                participants[3].getId(),
                                participants[4].getId(),
                                participants[5].getId(),
                                participants[6].getId()
                        ),
                () -> assertThat(attendances)
                        .map(Attendance::getStatus)
                        .containsExactly(
                                NON_ATTENDANCE,
                                ATTENDANCE,
                                ATTENDANCE,
                                ATTENDANCE,
                                ATTENDANCE,
                                NON_ATTENDANCE,
                                NON_ATTENDANCE,
                                NON_ATTENDANCE
                        )
        );
    }

    private void beParticipation() {
        for (Member member : participants) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    private void applyNonAttendanceToAllParticipants() {
        for (Member participant : participants) {
            attendanceRepository.save(Attendance.recordAttendance(study, participant, 1, NON_ATTENDANCE));
        }
    }
}
