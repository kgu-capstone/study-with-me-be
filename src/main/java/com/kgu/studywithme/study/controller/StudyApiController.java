package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;
import com.kgu.studywithme.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyApiController {
    private final StudyService studyService;

    @PostMapping("/study")
    public ResponseEntity<Void> register(@RequestBody @Valid StudyRegisterRequest request, @ExtractPayload Long hostId) {
        Long savedStudyId = studyService.register(request, hostId);
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/studies/{id}").build(savedStudyId))
                .build();
    }

    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<Void> update(@PathVariable Long studyId,
                                       @RequestBody @Valid StudyUpdateRequest request,
                                       @ExtractPayload Long hostId) {
        studyService.update(studyId, request, hostId);
        return ResponseEntity.noContent().build();
    }
}
