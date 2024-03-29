package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.domain.week.QWeek;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.infra.query.dto.response.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.kgu.studywithme.member.domain.QMember.member;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;
import static com.kgu.studywithme.study.domain.notice.QNotice.notice;
import static com.kgu.studywithme.study.domain.notice.comment.QComment.comment;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.study.domain.review.QReview.review;
import static com.kgu.studywithme.study.domain.week.submit.QSubmit.submit;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationQueryRepositoryImpl implements StudyInformationQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public int getGraduatedParticipantCountByStudyId(Long studyId) {
        return query
                .select(participant.id)
                .from(participant)
                .where(studyIdEq(studyId), graduateStatus())
                .fetch()
                .size();
    }

    @Override
    public List<ReviewInformation> findReviewByStudyId(Long studyId) {
        return query
                .select(new QReviewInformation(review.id, review.content, review.modifiedAt, member.id, member.nickname))
                .from(review)
                .innerJoin(review.writer, member)
                .where(review.study.id.eq(studyId))
                .orderBy(review.id.desc())
                .fetch();
    }

    @Override
    public List<NoticeInformation> findNoticeWithCommentsByStudyId(Long studyId) {
        List<NoticeInformation> noticeResult = query
                .select(new QNoticeInformation(
                        notice.id, notice.title, notice.content, notice.createdAt, notice.modifiedAt,
                        member.id, member.nickname
                ))
                .from(notice)
                .innerJoin(notice.writer, member)
                .where(notice.study.id.eq(studyId))
                .orderBy(notice.id.desc())
                .fetch();

        applyCommentsInNotice(noticeResult);
        return noticeResult;
    }

    private void applyCommentsInNotice(List<NoticeInformation> noticeResult) {
        List<CommentInformation> commentResult = query
                .select(new QCommentInformation(
                        comment.id, comment.notice.id, comment.content, comment.modifiedAt,
                        member.id, member.nickname
                ))
                .from(comment)
                .innerJoin(comment.writer, member)
                .orderBy(comment.id.asc())
                .fetch();

        noticeResult.forEach(notice -> notice.applyComments(
                commentResult.stream()
                        .filter(comment -> comment.getNoticeId().equals(notice.getId()))
                        .toList()
        ));
    }

    @Override
    public List<StudyApplicantInformation> findApplicantByStudyId(Long studyId) {
        return query
                .select(new QStudyApplicantInformation(member.id, member.nickname, member.score, participant.createdAt))
                .from(participant)
                .innerJoin(participant.member, member)
                .where(studyIdEq(studyId), applyStatus())
                .orderBy(member.id.desc())
                .fetch();
    }

    @Override
    public List<AttendanceInformation> findAttendanceByStudyId(Long studyId) {
        Long hostId = query
                .select(study.participants.host.id)
                .from(study)
                .where(study.id.eq(studyId))
                .fetchOne();

        List<Long> participantIds = query
                .select(participant.member.id)
                .from(participant)
                .where(
                        participant.study.id.eq(studyId),
                        participant.status.eq(APPROVE)
                )
                .fetch();

        List<Long> totalIds = Stream.of(List.of(hostId), participantIds)
                .flatMap(Collection::stream)
                .toList();

        return query
                .select(new QAttendanceInformation(member.id, member.nickname, attendance.week, attendance.status))
                .from(attendance)
                .innerJoin(attendance.participant, member)
                .where(
                        attendance.study.id.eq(studyId),
                        member.id.in(totalIds)
                )
                .orderBy(member.id.asc(), attendance.week.asc())
                .fetch();
    }

    @Override
    public List<Week> findWeeklyByStudyId(Long studyId) {
        QWeek weekly = new QWeek("week");

        return query
                .selectDistinct(weekly)
                .from(weekly)
                .leftJoin(weekly.submits, submit).fetchJoin()
                .leftJoin(submit.participant).fetchJoin()
                .where(weekly.study.id.eq(studyId))
                .orderBy(weekly.id.desc())
                .fetch();
    }

    private BooleanExpression studyIdEq(Long studyId) {
        return participant.study.id.eq(studyId);
    }

    private BooleanExpression graduateStatus() {
        return participant.status.eq(GRADUATED);
    }

    private BooleanExpression applyStatus() {
        return participant.status.eq(APPLY);
    }
}
