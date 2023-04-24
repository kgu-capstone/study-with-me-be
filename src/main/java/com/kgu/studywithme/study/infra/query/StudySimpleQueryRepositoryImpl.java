package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.member.domain.QMember;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.QBasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.QSimpleStudy;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudySimpleQueryRepositoryImpl implements StudySimpleQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember host = new QMember("host");

    @Override
    public List<BasicHashtag> findHashtags() {
        return query
                .select(new QBasicHashtag(study.id, hashtag.name))
                .from(study)
                .innerJoin(study.hashtags, hashtag)
                .fetch();
    }

    @Override
    public List<SimpleStudy> findParticipateStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleStudy(study.id, study.name, study.category, study.thumbnail))
                .from(study)
                .innerJoin(study.participants.host, host)
                .leftJoin(participant).on(participant.study.id.eq(study.id))
                .where(participateStatus(), hostOrParticipant(memberId))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleStudy> findGraduatedStudyByMemberId(Long memberId) {
        return query
                .select(new QSimpleStudy(study.id, study.name, study.category, study.thumbnail))
                .from(study)
                .innerJoin(participant).on(participant.study.id.eq(study.id))
                .where(graduateStatus(), graduatedMemberIdEq(memberId))
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

    private BooleanExpression participateStatus() {
        return participant.status.eq(APPROVE);
    }

    private BooleanExpression hostOrParticipant(Long memberId) {
        return (memberId != null) ? host.id.eq(memberId).or(participant.member.id.eq(memberId)) : null;
    }

    private BooleanExpression graduateStatus() {
        return participant.status.eq(GRADUATED);
    }

    private BooleanExpression graduatedMemberIdEq(Long memberId) {
        return (memberId != null) ? participant.member.id.eq(memberId) : null;
    }
}
