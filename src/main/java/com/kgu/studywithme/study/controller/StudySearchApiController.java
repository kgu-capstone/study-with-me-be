package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.StudyCategorySearchRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyRecommendSearchRequest;
import com.kgu.studywithme.study.service.StudySearchService;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kgu.studywithme.study.utils.PagingConstants.SLICE_PER_PAGE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies")
public class StudySearchApiController {
    private final StudySearchService studySearchService;

    @GetMapping
    public ResponseEntity<DefaultStudyResponse> findStudyByCategory(@ModelAttribute StudyCategorySearchRequest request) {
        DefaultStudyResponse result = studySearchService.findStudyByCategory(
                Category.from(request.category()), request.sort(), getDefaultPageRequest(request.page()), request.isOnline());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recommend")
    public ResponseEntity<DefaultStudyResponse> findStudyByRecommend(@ModelAttribute StudyRecommendSearchRequest request, @ExtractPayload Long memberId) {
        DefaultStudyResponse result = studySearchService.findStudyByRecommend(
                memberId, request.sort(), getDefaultPageRequest(request.page()), request.isOnline());
        return ResponseEntity.ok(result);
    }

    private Pageable getDefaultPageRequest(int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
