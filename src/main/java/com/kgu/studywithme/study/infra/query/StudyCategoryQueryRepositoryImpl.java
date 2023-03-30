package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.infra.query.dto.response.QBasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.interest.QInterest.interest;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.study.domain.review.QReview.review;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_DATE;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_FAVORITE;
import static com.querydsl.jpa.JPAExpressions.select;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCategoryQueryRepositoryImpl implements StudyCategoryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<BasicStudy> findStudyByCategory(StudyCategoryCondition condition, Pageable pageable) {
        List<BasicStudy> result = query
                .select(assembleStudyProjections())
                .from(study)
                .leftJoin(favorite).on(favorite.studyId.eq(study.id))
                .leftJoin(review).on(review.study.id.eq(study.id))
                .where(categoryEq(condition.category()), studyType(condition.isOnline()))
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int totalCount = query
                .select(study.id)
                .from(study)
                .where(categoryEq(condition.category()), studyType(condition.isOnline()))
                .fetch()
                .size();

        return new SliceImpl<>(result, pageable, validateHasNext(pageable,  result.size(), totalCount));
    }

    @Override
    public Slice<BasicStudy> findStudyByRecommend(StudyRecommendCondition condition, Pageable pageable) {
        List<Category> memberInterests = query
                .select(interest.category)
                .from(interest)
                .where(interest.member.id.eq(condition.memberId()))
                .fetch();

        List<BasicStudy> result = query
                .select(assembleStudyProjections())
                .from(study)
                .leftJoin(favorite).on(favorite.studyId.eq(study.id))
                .leftJoin(review).on(review.study.id.eq(study.id))
                .where(studyType(condition.isOnline()), studyCategoryIn(memberInterests))
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int totalCount = query
                .select(study.id)
                .from(study)
                .where(studyType(condition.isOnline()), studyCategoryIn(memberInterests))
                .fetch()
                .size();

        return new SliceImpl<>(result, pageable, validateHasNext(pageable,  result.size(), totalCount));
    }

    public static ConstructorExpression<BasicStudy> assembleStudyProjections() {
        return new QBasicStudy(
                study.id, study.name, study.description, study.category, study.type, study.recruitmentStatus,
                select(participant.count().add(1))
                        .from(participant)
                        .where(participant.study.id.eq(study.id).and(participant.status.eq(APPROVE))),
                study.participants.capacity, study.createdAt, favorite.count(), review.count()
        );
    }

    private List<OrderSpecifier<?>> orderBySortType(String sort) {
        List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sort) {
            case SORT_DATE -> orderBy.add(study.id.desc()); // 최신순
            case SORT_FAVORITE -> orderBy.addAll(List.of(favorite.count().desc(), study.id.desc())); // 찜 많은 순
            default -> orderBy.addAll(List.of(review.count().desc(), study.id.desc())); // 리뷰 많은 순
        }

        return orderBy;
    }

    private boolean validateHasNext(Pageable pageable, int contentSize, int totalCount) {
        if (contentSize == pageable.getPageSize()) {
            return contentSize * (pageable.getPageNumber() + 1) != totalCount;
        }

        return false;
    }

    private BooleanExpression categoryEq(Category category) {
        return (category != null) ? study.category.eq(category) : null;
    }

    private BooleanExpression studyType(boolean isOnline) {
        return isOnline ? study.type.eq(ONLINE) : study.type.eq(OFFLINE);
    }

    private BooleanExpression studyCategoryIn(List<Category> memberInterests) {
        return (memberInterests != null) ? study.category.in(memberInterests) : null;
    }
}
