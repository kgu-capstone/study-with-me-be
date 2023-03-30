package com.kgu.studywithme.study.service;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.service.dto.response.StudyAssembler;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
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

    public DefaultStudyResponse findStudyByCategory(Category category, String sort, Pageable pageable, boolean isOnline) {
        StudyCategoryCondition condition = new StudyCategoryCondition(category, sort, isOnline);
        Slice<BasicStudy> paging = studyRepository.findStudyWithCondition(condition, pageable);
        return assemblingResult(paging);
    }

    private DefaultStudyResponse assemblingResult(Slice<BasicStudy> paging) {
        List<BasicHashtag> basicHashtags = studyRepository.findHashtags();
        List<StudyAssembler> result = paging.getContent()
                .stream()
                .map(study -> new StudyAssembler(study, collectHashtags(basicHashtags, study)))
                .toList();

        return new DefaultStudyResponse(result, paging.hasNext());
    }

    private List<String> collectHashtags(List<BasicHashtag> basicHashtags, BasicStudy study) {
        return basicHashtags
                .stream()
                .filter(hashtag -> hashtag.studyId().equals(study.getId()))
                .map(BasicHashtag::name)
                .toList();
    }
}
