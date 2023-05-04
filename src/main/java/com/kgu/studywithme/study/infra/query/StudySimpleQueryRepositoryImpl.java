package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.member.domain.QMember;
import com.kgu.studywithme.study.domain.participant.ParticipantStatus;
import com.kgu.studywithme.study.domain.week.QWeek;
import com.kgu.studywithme.study.infra.query.dto.response.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudySimpleQueryRepositoryImpl implements StudySimpleQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember host = new QMember("host");

    @Override
    public List<SimpleStudy> findApplyStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleStudy(study.id, study.name, study.category, study.thumbnail))
                .from(study)
                .innerJoin(participant).on(participant.study.id.eq(study.id))
                .where(memberIdEq(memberId), participateStatusEq(APPLY))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleStudy> findParticipateStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleStudy(study.id, study.name, study.category, study.thumbnail))
                .from(study)
                .innerJoin(study.participants.host, host)
                .leftJoin(participant).on(participant.study.id.eq(study.id))
                .where(hostOrParticipant(memberId), participateStatusEq(APPROVE))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleStudy> findFavoriteStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleStudy(study.id, study.name, study.category, study.thumbnail))
                .from(study)
                .innerJoin(favorite).on(favorite.studyId.eq(study.id))
                .where(favorite.memberId.eq(memberId))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleStudy> findGraduatedStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleStudy(study.id, study.name, study.category, study.thumbnail))
                .from(study)
                .innerJoin(participant).on(participant.study.id.eq(study.id))
                .where(memberIdEq(memberId), participateStatusEq(GRADUATED))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<BasicWeekly> findAutoAttendanceAndPeriodEndWeek() {
        final LocalDateTime now = LocalDateTime.now();
        QWeek weekly = new QWeek("week");

        return query
                .select(new QBasicWeekly(weekly.study.id, weekly.week))
                .from(weekly)
                .where(
                        weekly.autoAttendance.eq(true),
                        weekly.period.endDate.before(now)
                )
                .orderBy(weekly.study.id.asc(), weekly.week.asc())
                .fetch();
    }

    @Override
    public List<BasicAttendance> findBasicAttendanceInformation() {
        return query
                .select(new QBasicAttendance(attendance.study.id, attendance.week, attendance.participant.id))
                .from(attendance)
                .where(attendance.status.eq(NON_ATTENDANCE))
                .orderBy(attendance.study.id.asc())
                .fetch();
    }

    private BooleanExpression participateStatusEq(ParticipantStatus status) {
        return (status != null) ? participant.status.eq(status) : null;
    }

    private BooleanExpression hostOrParticipant(Long memberId) {
        return (memberId != null) ? host.id.eq(memberId).or(participant.member.id.eq(memberId)) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return (memberId != null) ? participant.member.id.eq(memberId) : null;
    }
}
