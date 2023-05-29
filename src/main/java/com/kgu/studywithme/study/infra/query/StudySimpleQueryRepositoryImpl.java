package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.member.domain.QMember;
import com.kgu.studywithme.study.domain.participant.ParticipantStatus;
import com.kgu.studywithme.study.domain.week.QWeek;
import com.kgu.studywithme.study.infra.query.dto.response.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.study.domain.review.QReview.review;
import static com.kgu.studywithme.study.domain.week.attachment.QAttachment.attachment;
import static com.kgu.studywithme.study.domain.week.submit.QSubmit.submit;

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
                .where(hostIdEq(memberId).or(participantIdEqAndApproveStatus(memberId)))
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
    public List<SimpleGraduatedStudy> findGraduatedStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleGraduatedStudy(
                        study.id, study.name, study.category, study.thumbnail,
                        review.id, review.content, review.createdAt, review.modifiedAt
                ))
                .from(study)
                .innerJoin(participant).on(participant.study.id.eq(study.id))
                .leftJoin(review).on(review.study.id.eq(study.id))
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
    public List<BasicAttendance> findNonAttendanceInformation() {
        return query
                .select(new QBasicAttendance(attendance.study.id, attendance.week, attendance.participant.id))
                .from(attendance)
                .where(attendance.status.eq(NON_ATTENDANCE))
                .orderBy(attendance.study.id.asc())
                .fetch();
    }

    @Override
    public boolean isStudyParticipant(Long studyId, Long memberId) {
        List<Long> participantIds = query
                .select(participant.member.id)
                .from(participant)
                .where(
                        participant.study.id.eq(studyId),
                        participateStatusEq(APPROVE)
                )
                .fetch();

        Long count = query
                .select(study.count())
                .from(study)
                .where(hostIdEq(memberId).or(isMemberInParticipant(memberId, participantIds)))
                .fetchOne();

        return count > 0;
    }

    @Override
    public int getNextWeek(Long studyId) {
        QWeek weekly = new QWeek("week");

        List<Integer> weeks = query
                .select(weekly.week)
                .from(weekly)
                .where(weekly.study.id.eq(studyId))
                .orderBy(weekly.week.desc())
                .fetch();

        if (weeks.size() == 0) {
            return 1;
        }

        return weeks.get(0) + 1;
    }

    @Override
    public boolean isLatestWeek(Long studyId, Integer week) {
        QWeek weekly = new QWeek("week");

        List<Integer> weeks = query
                .select(weekly.week)
                .from(weekly)
                .where(weekly.study.id.eq(studyId))
                .orderBy(weekly.week.desc())
                .fetch();

        if (weeks.size() == 0) {
            return true;
        }

        return weeks.get(0).equals(week);
    }

    @Override
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    public void deleteSpecificWeek(Long studyId, Integer week) {
        QWeek weekly = new QWeek("week");

        Long weekId = query
                .select(weekly.id)
                .from(weekly)
                .where(
                        weekly.study.id.eq(studyId),
                        weekly.week.eq(week)
                )
                .fetchOne();

        query.delete(submit)
                .where(submit.week.id.eq(weekId))
                .execute();

        query.delete(attachment)
                .where(attachment.week.id.eq(weekId))
                .execute();

        query.delete(weekly)
                .where(weekly.id.eq(weekId))
                .execute();
    }

    private BooleanExpression participateStatusEq(ParticipantStatus status) {
        return (status != null) ? participant.status.eq(status) : null;
    }

    private BooleanExpression participantIdEqAndApproveStatus(Long memberId) {
        return (memberId != null) ? participant.member.id.eq(memberId).and(participateStatusEq(APPROVE)) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return (memberId != null) ? participant.member.id.eq(memberId) : null;
    }

    private BooleanExpression hostIdEq(Long memberId) {
        return (memberId != null) ? study.participants.host.id.eq(memberId) : null;
    }

    private BooleanExpression isMemberInParticipant(Long memberId, List<Long> participantIds) {
        return !CollectionUtils.isEmpty(participantIds) ? Expressions.asNumber(memberId).in(participantIds) : null;
    }
}
