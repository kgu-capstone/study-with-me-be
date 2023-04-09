package com.kgu.studywithme.member.domain.report;

import com.kgu.studywithme.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.kgu.studywithme.member.domain.report.ReportStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_report")
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reportee_id", nullable = false)
    private Long reporteeId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Lob
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Builder
    private Report(Long reporteeId, Long reporterId, String reason) {
        this.reporteeId = reporteeId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.status = RECEIVE;
    }

    public static Report createReportWithReason(Long reporteeId, Long reporterId, String reason) {
        return new Report(reporteeId, reporterId, reason);
    }

    public void approveReport() {
        this.status = APPROVE;
    }

    public void rejectReport() {
        this.status = REJECT;
    }
}
