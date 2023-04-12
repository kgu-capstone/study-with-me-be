package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberReportService 테스트")
class MemberReportServiceTest extends ServiceTest {
    @Autowired
    private MemberReportService memberReportService;

    private Member reportee;
    private Member reporter;

    @BeforeEach
    void setUp() {
        reportee = memberRepository.save(GHOST.toMember());
        reporter = memberRepository.save(JIWON.toMember());
    }

    @Nested
    @DisplayName("사용자 신고")
    class report {
        @Test
        @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
        void failureByPreviousReportIsStillPending() {
            // given
            memberReportService.report(reportee.getId(), reporter.getId(), "참여를 안해요");

            // when - then
            assertThatThrownBy(() -> memberReportService.report(reportee.getId(), reporter.getId(), "과제를 제출안해요"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.REPORT_IS_STILL_RECEIVED.getMessage());
        }

        @Test
        @DisplayName("사용자 신고에 성공한다")
        void success() {
            // given
            final String reason = "참여를 안해요";

            // when
            Long reportId = memberReportService.report(reportee.getId(), reporter.getId(), reason);

            // then
            Report findReport = reportRepository.findById(reportId).orElseThrow();
            assertAll(
                    () -> assertThat(findReport.getReporteeId()).isEqualTo(reportee.getId()),
                    () -> assertThat(findReport.getReporterId()).isEqualTo(reporter.getId()),
                    () -> assertThat(findReport.getReason()).isEqualTo(reason),
                    () -> assertThat(findReport.getStatus()).isEqualTo(RECEIVE)
            );
        }
    }
}
