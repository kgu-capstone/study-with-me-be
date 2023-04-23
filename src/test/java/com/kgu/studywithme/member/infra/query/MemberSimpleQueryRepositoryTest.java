package com.kgu.studywithme.member.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.infra.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infra.query.dto.response.StudyAttendanceMetadata;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Repository Layer] -> MemberSimpleQueryRepository 테스트")
class MemberSimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member participant;
    private Study study1;
    private Study study2;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        participant = memberRepository.save(GHOST.toMember());

        study1 = studyRepository.save(SPRING.toOnlineStudy(host));
        study1.applyParticipation(participant);
        study1.approveParticipation(participant);

        study2 = studyRepository.save(JPA.toOnlineStudy(host));
        study2.applyParticipation(participant);
        study2.approveParticipation(participant);
    }

    @Test
    @DisplayName("특정 사용자에 대한 신고가 접수 상태인지 확인한다")
    void isReportReceived() {
        // given
        Report report = reportRepository.save(Report.createReportWithReason(host.getId(), participant.getId(), "스터디를 대충합니다."));

        // 1. 신고 접수
        assertThat(memberRepository.isReportReceived(host.getId(), participant.getId())).isTrue();

        // 2. 신고 승인
        report.approveReport();
        assertThat(memberRepository.isReportReceived(host.getId(), participant.getId())).isFalse();

        // 3. 신고 거부
        report.rejectReport();
        assertThat(memberRepository.isReportReceived(host.getId(), participant.getId())).isFalse();
    }

    @Test
    @DisplayName("참여자 ID를 통해서 스터디 ID + 해당 스터디의 출석 정보를 조회한다")
    void findParticipationWeekByMemberId() {
        // given
        List<Integer> hostWeek1 = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> hostWeek2 = List.of(4, 5, 6);
        applyAttendance(study1, host, hostWeek1);
        applyAttendance(study2, host, hostWeek2);

        List<Integer> participantWeek1 = List.of(3, 4, 5);
        List<Integer> participantWeek2 = List.of(1, 2, 3, 4);
        applyAttendance(study1, participant, participantWeek1);
        applyAttendance(study2, participant, participantWeek2);

        // when - then
        List<StudyAttendanceMetadata> hostMetadata = memberRepository.findStudyAttendanceMetadataByMemberId(host.getId());
        List<Study> expectHostStudy = List.of(study1, study1, study1, study1, study1, study1, study2, study2, study2);
        List<Integer> expectHostWeek = List.of(1, 2, 3, 4, 5, 6, 4, 5, 6);
        assertThatParticipationMetadataMatch(hostMetadata, expectHostStudy, expectHostWeek);

        List<StudyAttendanceMetadata> participantMetadata = memberRepository.findStudyAttendanceMetadataByMemberId(participant.getId());
        List<Study> expectParticipantStudy = List.of(study1, study1, study1, study2, study2, study2, study2);
        List<Integer> expectParticipantWeek = List.of(3, 4, 5, 1, 2, 3, 4);
        assertThatParticipationMetadataMatch(participantMetadata, expectParticipantStudy, expectParticipantWeek);
    }

    @Test
    @DisplayName("참여자의 출석 현황을 조회한다")
    void getAttendanceCount() {
        /* 미출석 1회 */
        study1.recordAttendance(host, 1, NON_ATTENDANCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ATTENDANCE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), LATE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 1회 */
        study1.recordAttendance(host, 2, ATTENDANCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), LATE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 2회 */
        study1.recordAttendance(host, 3, ATTENDANCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ATTENDANCE)).isEqualTo(2),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), LATE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 2회 + 지각 1회 */
        study1.recordAttendance(host, 4, LATE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ATTENDANCE)).isEqualTo(2),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), LATE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 2회 + 지각 1회 + 결석 1회 */
        study1.recordAttendance(host, 5, ABSENCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ATTENDANCE)).isEqualTo(2),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), LATE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(study1.getId(), host.getId(), ABSENCE)).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("참여자의 출석률을 조회한다")
    void findAttendanceRatioByMemberId() {
        /* Week 1 */
        study1.recordAttendance(host, 1, NON_ATTENDANCE);
        study2.recordAttendance(host, 1, ATTENDANCE);
        List<AttendanceRatio> result1 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertAll(
                () -> assertThat(result1).hasSize(4),
                () -> assertThat(findCountByStatus(result1, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result1, ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result1, LATE)).isEqualTo(0),
                () -> assertThat(findCountByStatus(result1, ABSENCE)).isEqualTo(0)
        );

        /* Week 2 */
        study1.recordAttendance(host, 2, ATTENDANCE);
        study2.recordAttendance(host, 2, LATE);
        List<AttendanceRatio> result2 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertAll(
                () -> assertThat(result2).hasSize(4),
                () -> assertThat(findCountByStatus(result2, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result2, ATTENDANCE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result2, LATE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result2, ABSENCE)).isEqualTo(0)
        );

        /* Week 3 */
        study1.recordAttendance(host, 3, ATTENDANCE);
        study2.recordAttendance(host, 3, ATTENDANCE);
        List<AttendanceRatio> result3 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertAll(
                () -> assertThat(result3).hasSize(4),
                () -> assertThat(findCountByStatus(result3, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result3, ATTENDANCE)).isEqualTo(4),
                () -> assertThat(findCountByStatus(result3, LATE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result3, ABSENCE)).isEqualTo(0)
        );

        /* Week 4 */
        study1.recordAttendance(host, 4, LATE);
        study2.recordAttendance(host, 4, ABSENCE);
        List<AttendanceRatio> result4 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertAll(
                () -> assertThat(result4).hasSize(4),
                () -> assertThat(findCountByStatus(result4, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result4, ATTENDANCE)).isEqualTo(4),
                () -> assertThat(findCountByStatus(result4, LATE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result4, ABSENCE)).isEqualTo(1)
        );

        /* Week 5 */
        study1.recordAttendance(host, 5, ABSENCE);
        study2.recordAttendance(host, 5, NON_ATTENDANCE);
        List<AttendanceRatio> result5 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertAll(
                () -> assertThat(result5).hasSize(4),
                () -> assertThat(findCountByStatus(result5, NON_ATTENDANCE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result5, ATTENDANCE)).isEqualTo(4),
                () -> assertThat(findCountByStatus(result5, LATE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result5, ABSENCE)).isEqualTo(2)
        );
    }

    private void applyAttendance(Study study, Member member, List<Integer> weeks) {
        for (int week : weeks) {
            study.recordAttendance(member, week, ATTENDANCE);
        }
    }

    private void assertThatParticipationMetadataMatch(List<StudyAttendanceMetadata> metadata,
                                                      List<Study> studies,
                                                      List<Integer> weeks) {
        int totalSize = weeks.size();
        assertThat(metadata).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            StudyAttendanceMetadata specificMetadata = metadata.get(i);
            Study study = studies.get(i);
            int week = weeks.get(i);

            assertAll(
                    () -> assertThat(specificMetadata.studyId()).isEqualTo(study.getId()),
                    () -> assertThat(specificMetadata.week()).isEqualTo(week)
            );
        }
    }

    private int findCountByStatus(List<AttendanceRatio> attendanceRatios, AttendanceStatus status) {
        return attendanceRatios.stream()
                .filter(ratio -> ratio.status() == status)
                .findFirst()
                .map(AttendanceRatio::count)
                .orElse(0);
    }
}
