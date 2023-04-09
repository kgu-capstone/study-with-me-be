package com.kgu.studywithme.member.infra.query;

import com.kgu.studywithme.member.domain.report.ReportStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.kgu.studywithme.member.domain.report.QReport.report;
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberSimpleQueryRepositoryImpl implements MemberSimpleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isReportReceived(Long reporteeId, Long reporterId) {
        ReportStatus status = query
                .select(report.status)
                .from(report)
                .where(report.reporteeId.eq(reporteeId).and(report.reporterId.eq(reporterId)))
                .fetchOne();

        return status == RECEIVE;
    }
}
