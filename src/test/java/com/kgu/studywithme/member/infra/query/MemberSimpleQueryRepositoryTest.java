package com.kgu.studywithme.member.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.infra.query.dto.response.StudyAttendanceMetadata;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;
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
        applyAttandance(study1, host, hostWeek1);
        applyAttandance(study2, host, hostWeek2);

        List<Integer> participantWeek1 = List.of(3, 4, 5);
        List<Integer> participantWeek2 = List.of(1, 2, 3, 4);
        applyAttandance(study1, participant, participantWeek1);
        applyAttandance(study2, participant, participantWeek2);

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

    private void applyAttandance(Study study, Member member, List<Integer> weeks) {
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
}
