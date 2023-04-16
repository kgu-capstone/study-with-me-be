package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import com.kgu.studywithme.study.infra.query.dto.response.StudyWeeksDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> AttendanceQueryRepository 테스트")
public class AttendanceQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WeekRepository weekRepository;

    private Member host;
    private Member outsider;
    private final Member[] participants = new Member[2];
    private Study study1;
    private Study study2;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        outsider = memberRepository.save(GHOST.toMember());
        participants[0] = memberRepository.save(DUMMY1.toMember());
        participants[1] = memberRepository.save(DUMMY2.toMember());
        study1 = studyRepository.save(SPRING.toOnlineStudy(host));
        study2 = studyRepository.save(TOEIC.toOnlineStudy(host));

        beParticipation(study1, participants[0], participants[1]);

        /* study1 */
        // WEEK 1
        weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study1));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study1, host));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study1, participants[0]));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study1, participants[1]));

        // WEEK 2
        weekRepository.save(STUDY_WEEKLY_2.toWeekWithAssignment(study1));
        study1.graduateParticipant(participants[1]);
        attendanceRepository.save(Attendance.recordAttendance(2, NON_ATTENDANCE, study1, host));
        attendanceRepository.save(Attendance.recordAttendance(2, NON_ATTENDANCE, study1, participants[0]));

        /* study 2 */
        // WEEK 1
        weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study2));
        attendanceRepository.save(Attendance.recordAttendance(1, NON_ATTENDANCE, study2, host));
    }

    @Test
    @DisplayName("참여자 ID로 스터디 ID와 출석 정보를 조회한다")
    void findStudyIdAndWeekByParticipantId() {
        List<StudyWeeksDTO> hostDtos = attendanceRepository.findStudyIdAndWeekByParticipantId(host.getId());
        List<StudyWeeksDTO> participant0Dtos = attendanceRepository.findStudyIdAndWeekByParticipantId(participants[0].getId());
        List<StudyWeeksDTO> participant1Dtos = attendanceRepository.findStudyIdAndWeekByParticipantId(participants[1].getId());
        List<StudyWeeksDTO> outsiderDtos = attendanceRepository.findStudyIdAndWeekByParticipantId(outsider.getId());

        assertAll(
                () -> assertThat(hostDtos.size()).isEqualTo(2),
                () -> assertThat(hostDtos.get(0).getWeeks()).containsAll(List.of(1, 2)),
                () -> assertThat(hostDtos.get(1).getWeeks()).containsAll(List.of(1))
        );
        assertAll(
                () -> assertThat(participant0Dtos.size()).isEqualTo(1),
                () -> assertThat(participant0Dtos.get(0).getWeeks()).containsAll(List.of(1, 2))
        );
        assertAll(
                () -> assertThat(participant1Dtos.size()).isEqualTo(1),
                () -> assertThat(participant1Dtos.get(0).getWeeks()).containsAll(List.of(1))
        );
        assertAll(
                () -> assertThat(outsiderDtos.size()).isEqualTo(0)
        );
    }

    private void beParticipation(Study study, Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }
}
