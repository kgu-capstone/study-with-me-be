package com.kgu.studywithme.member.infra.query;

import com.kgu.studywithme.member.domain.report.ReportStatus;
import com.kgu.studywithme.member.infra.query.dto.response.QStudyAttendanceMetadata;
import com.kgu.studywithme.member.infra.query.dto.response.StudyAttendanceMetadata;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kgu.studywithme.member.domain.report.QReport.report;
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;
import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;

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

    @Override
    public List<StudyAttendanceMetadata> findStudyAttendanceMetadataByMemberId(Long memberId) {
        return query
                .select(new QStudyAttendanceMetadata(attendance.study.id, attendance.week))
                .from(attendance)
                .where(participantIdEq(memberId))
                .orderBy(attendance.study.id.asc())
                .fetch();
    }

    @Override
    public Long getAttendanceCount(Long studyId, Long memberId, AttendanceStatus status) {
        return query
                .select(attendance.count())
                .from(attendance)
                .where(
                        attendance.study.id.eq(studyId),
                        participantIdEq(memberId),
                        attendance.status.eq(status)
                )
                .fetchOne();
    }

    private BooleanExpression participantIdEq(Long memberId) {
        return attendance.participant.id.eq(memberId);
    }
}
