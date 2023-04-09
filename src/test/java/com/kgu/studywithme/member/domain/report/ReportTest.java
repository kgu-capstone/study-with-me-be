package com.kgu.studywithme.member.domain.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.member.domain.report.ReportStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member-Report 도메인 테스트")
class ReportTest {
    private static final String REASON = "무단 결석을 10회나 했어요. 계정 정지시켜주세요.";

    @Test
    @DisplayName("특정 사용자에 대한 신고를 진행한다")
    void construct() {
        // when
        Report report = Report.createReportWithReason(1L, 2L, REASON);

        // then
        assertAll(
                () -> assertThat(report.getStatus()).isEqualTo(RECEIVE),
                () -> assertThat(report.getReason()).isEqualTo(REASON)
        );
    }

    @Test
    @DisplayName("관리자가 접수된 신고를 승인한다")
    void approve() {
        // given
        Report report = Report.createReportWithReason(1L, 2L, REASON);

        // when
        report.approveReport();

        // then
        assertThat(report.getStatus()).isEqualTo(APPROVE);
    }

    @Test
    @DisplayName("관리자가 접수된 신고를 거부한다")
    void reject() {
        // given
        Report report = Report.createReportWithReason(1L, 2L, REASON);

        // when
        report.rejectReport();

        // then
        assertThat(report.getStatus()).isEqualTo(REJECT);
    }
}
