package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.StudyCategorySearchRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyRecommendSearchRequest;
import com.kgu.studywithme.study.service.StudySearchService;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kgu.studywithme.study.utils.PagingConstants.getDefaultPageRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies")
public class StudySearchApiController {
    private final StudySearchService studySearchService;

    @GetMapping
    public ResponseEntity<DefaultStudyResponse> findStudyByCategory(@ModelAttribute StudyCategorySearchRequest request) {
        StudyCategoryCondition condition = new StudyCategoryCondition(request);
        DefaultStudyResponse result = studySearchService.findStudyByCategory(condition, getDefaultPageRequest(request.page()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recommend")
    public ResponseEntity<DefaultStudyResponse> findStudyByRecommend(@ModelAttribute StudyRecommendSearchRequest request,
                                                                     @ExtractPayload Long memberId) {
        StudyRecommendCondition condition = new StudyRecommendCondition(request, memberId);
        DefaultStudyResponse result = studySearchService.findStudyByRecommend(condition, getDefaultPageRequest(request.page()));
        return ResponseEntity.ok(result);
    }
}
