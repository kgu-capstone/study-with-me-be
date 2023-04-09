package com.kgu.studywithme.member.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member [Repository Layer] -> ReportRepository 테스트")
class MemberSimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReportRepository reportRepository;

    private Member reportee;
    private Member reporter;
    private Report report;

    @BeforeEach
    void setUp() {
        reportee = memberRepository.save(JIWON.toMember());
        reporter = memberRepository.save(GHOST.toMember());

        final String reason = "스터디를 대충합니다.";
        report = reportRepository.save(Report.createReportWithReason(reportee.getId(), reporter.getId(), reason));
    }

    @Test
    @DisplayName("특정 사용자에 대한 신고가 접수 상태인지 확인한다")
    void isReportReceived() {
        // 1. 신고 접수
        assertThat(memberRepository.isReportReceived(reportee.getId(), reporter.getId())).isTrue();

        // 2. 신고 승인
        report.approveReport();
        assertThat(memberRepository.isReportReceived(reportee.getId(), reporter.getId())).isFalse();

        // 3. 신고 거부
        report.rejectReport();
        assertThat(memberRepository.isReportReceived(reportee.getId(), reporter.getId())).isFalse();
    }
}
