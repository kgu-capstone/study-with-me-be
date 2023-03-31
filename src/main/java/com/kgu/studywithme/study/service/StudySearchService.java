package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudySearchService {
    private final StudyRepository studyRepository;

    public DefaultStudyResponse findStudyByCategory(StudyCategoryCondition condition, Pageable pageable) {
        Slice<BasicStudy> result = studyRepository.findStudyByCategory(condition, pageable);
        return assemblingResult(result);
    }

    public DefaultStudyResponse findStudyByRecommend(StudyRecommendCondition condition, Pageable pageable) {
        Slice<BasicStudy> result = studyRepository.findStudyByRecommend(condition, pageable);
        return assemblingResult(result);
    }

    private DefaultStudyResponse assemblingResult(Slice<BasicStudy> result) {
        List<BasicHashtag> basicHashtags = studyRepository.findHashtags();
        result.getContent()
                .forEach(study -> study.setHashtags(collectHashtags(basicHashtags, study)));

        return new DefaultStudyResponse(result.getContent(), result.hasNext());
    }

    private List<String> collectHashtags(List<BasicHashtag> basicHashtags, BasicStudy study) {
        return basicHashtags
                .stream()
                .filter(hashtag -> hashtag.studyId().equals(study.getId()))
                .map(BasicHashtag::name)
                .toList();
    }
}
