package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.category.service.CategoryService;
import com.kgu.studywithme.category.service.dto.response.CategoryResponse;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyRequest;
import com.kgu.studywithme.study.service.StudyService;
import com.kgu.studywithme.study.service.dto.response.StudyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyApiController {
    private final StudyService studyService;

    // 메인 페이지에서 스터디 리스트, 카테고리 별로
    @GetMapping
    public ResponseEntity<SimpleResponseWrapper<List<StudyResponse>>> findAllWithCategory(@RequestBody Category category) {
        return ResponseEntity.ok(new SimpleResponseWrapper<>(studyService.findAllWithCategory(category)));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<SimpleResponseWrapper<List<StudyResponse>>> findAllWithCategory(@RequestBody Category category) {
//        return ResponseEntity.ok(new SimpleResponseWrapper<>(studyService.findAllWithCategory(category)));
//    }

    @PostMapping("/build")
    public ResponseEntity<Void> buildStudy(@RequestBody @Valid StudyRequest request) {
        Long savedStudyId = studyService.build(request.toEntity(), request.toInterestCategories());
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/study/{id}").build(savedStudyId))
                .build();
    }
}
