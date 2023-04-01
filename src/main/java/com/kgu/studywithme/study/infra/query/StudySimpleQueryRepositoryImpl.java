package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.QBasicHashtag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudySimpleQueryRepositoryImpl implements StudySimpleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<BasicHashtag> findHashtags() {
        return query
                .select(new QBasicHashtag(study.id, hashtag.name))
                .from(study)
                .innerJoin(study.hashtags, hashtag)
                .fetch();
    }

    @Override
    public List<String> findHashtagsByStudyId(Long studyId) {
        return query
                .select(hashtag.name)
                .from(study)
                .innerJoin(study.hashtags, hashtag)
                .where(study.id.eq(studyId))
                .fetch();
    }
}
