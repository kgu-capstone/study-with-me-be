package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.infra.query.dto.response.QBasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.QBasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.interest.QInterest.interest;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.study.domain.review.QReview.review;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_FAVORITE;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_REVIEW;
import static com.querydsl.jpa.JPAExpressions.select;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCategoryQueryRepositoryImpl implements StudyCategoryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<BasicStudy> findStudyByCategory(StudyCategoryCondition condition, Pageable pageable) {
        JPAQuery<BasicStudy> fetchQuery = query
                .select(assembleStudyProjections())
                .from(study)
                .where(
                        categoryEq(condition.category()),
                        studyType(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city())
                )
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<BasicStudy> result = makeFetchQueryResult(fetchQuery, condition.sort());
        Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        categoryEq(condition.category()),
                        studyType(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city())
                )
                .fetchOne();

        return new SliceImpl<>(result, pageable, validateHasNext(pageable,  result.size(), totalCount));
    }

    @Override
    public Slice<BasicStudy> findStudyByRecommend(StudyRecommendCondition condition, Pageable pageable) {
        List<Category> memberInterests = query
                .select(interest.category)
                .from(interest)
                .where(interest.member.id.eq(condition.memberId()))
                .fetch();

        JPAQuery<BasicStudy> fetchQuery = query
                .select(assembleStudyProjections())
                .from(study)
                .leftJoin(participant).on(
                        participant.study.id.eq(study.id),
                        participant.status.eq(APPROVE)
                )
                .where(
                        studyType(condition.type()),
                        studyCategoryIn(memberInterests),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city())
                )
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<BasicStudy> result = makeFetchQueryResult(fetchQuery, condition.sort());
        Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        studyType(condition.type()),
                        studyCategoryIn(memberInterests),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city())
                )
                .fetchOne();

        return new SliceImpl<>(result, pageable, validateHasNext(pageable,  result.size(), totalCount));
    }

    public static ConstructorExpression<BasicStudy> assembleStudyProjections() {
        return new QBasicStudy(
                study.id, study.name, study.description, study.category, study.thumbnail, study.type, study.recruitmentStatus,
                select(participant.count().intValue())
                        .from(participant)
                        .where(
                                participant.study.id.eq(study.id),
                                participant.status.eq(APPROVE)
                        ),
                study.participants.capacity, study.createdAt
        );
    }

    private List<OrderSpecifier<?>> orderBySortType(String sort) {
        List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sort) {
            case SORT_FAVORITE -> orderBy.addAll(List.of(favorite.count().desc(), study.id.desc()));
            case SORT_REVIEW -> orderBy.addAll(List.of(review.count().desc(), study.id.desc()));
            default -> orderBy.add(study.id.desc());
        }

        return orderBy;
    }

    private List<BasicStudy> makeFetchQueryResult(JPAQuery<BasicStudy> fetchQuery, String sort) {
        List<BasicStudy> result = addJoinBySortOption(fetchQuery, sort);
        List<Long> studyIds = result.stream()
                .map(BasicStudy::getId)
                .toList();

        applyStudyHashtags(studyIds, result);
        applyStudyFavoriteMarkingMembers(studyIds, result);
        return result;
    }

    private List<BasicStudy> addJoinBySortOption(JPAQuery<BasicStudy> fetchQuery, String sort) {
        return switch (sort) {
            case SORT_FAVORITE -> fetchQuery
                    .leftJoin(favorite).on(favorite.studyId.eq(study.id))
                    .fetch();
            case SORT_REVIEW -> fetchQuery
                    .leftJoin(review).on(review.study.id.eq(study.id))
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private void applyStudyHashtags(List<Long> studyIds, List<BasicStudy> result) {
        List<BasicHashtag> hashtags = query
                .select(new QBasicHashtag(study.id, hashtag.name))
                .from(hashtag)
                .innerJoin(study).on(study.id.eq(hashtag.study.id))
                .where(study.id.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyHashtags(collectHashtags(hashtags, study)));
    }

    private List<String> collectHashtags(List<BasicHashtag> hashtags, BasicStudy study) {
        return hashtags
                .stream()
                .filter(hashtag -> hashtag.studyId().equals(study.getId()))
                .map(BasicHashtag::name)
                .toList();
    }

    private void applyStudyFavoriteMarkingMembers(List<Long> studyIds, List<BasicStudy> result) {
        List<Favorite> favorites = query
                .select(favorite)
                .from(favorite)
                .where(favorite.studyId.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyFavoriteMarkingMembers(collectFavoriteMarkingMembers(favorites, study)));
    }

    private List<Long> collectFavoriteMarkingMembers(List<Favorite> favorites, BasicStudy study) {
        return favorites
                .stream()
                .filter(favorite -> favorite.getStudyId().equals(study.getId()))
                .map(Favorite::getMemberId)
                .toList();

    }

    private boolean validateHasNext(Pageable pageable, int contentSize, Long totalCount) {
        if (contentSize == pageable.getPageSize()) {
            return (long) contentSize * (pageable.getPageNumber() + 1) != totalCount;
        }

        return false;
    }

    private BooleanExpression categoryEq(Category category) {
        return (category != null) ? study.category.eq(category) : null;
    }

    private BooleanExpression studyType(String type) {
        if (!hasText(type)) {
            return null;
        }

        return ONLINE.getBrief().equalsIgnoreCase(type) ? study.type.eq(ONLINE) : study.type.eq(OFFLINE);
    }

    private BooleanExpression studyCategoryIn(List<Category> memberInterests) {
        return (memberInterests != null) ? study.category.in(memberInterests) : null;
    }

    private BooleanExpression studyLocationProvinceEq(String province) {
        return hasText(province) ? study.location.province.eq(province) : null;
    }

    private BooleanExpression studyLocationCityEq(String city) {
        return hasText(city) ? study.location.city.eq(city) : null;
    }

    private boolean hasText(String str) {
        return StringUtils.hasText(str);
    }
}
