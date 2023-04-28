package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.annotation.aop.CheckStudyHost;
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
    public ResponseEntity<Void> register(@ExtractPayload Long hostId,
                                         @RequestBody @Valid StudyRegisterRequest request) {
        Long savedStudyId = studyService.register(hostId, request);
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/studies/{id}").build(savedStudyId))
                .build();
    }

    @CheckStudyHost
    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<Void> update(@ExtractPayload Long hostId,
                                       @PathVariable Long studyId,
                                       @RequestBody @Valid StudyUpdateRequest request) {
        studyService.update(studyId, hostId, request);
        return ResponseEntity.noContent().build();
    }

    @CheckStudyHost
    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> close(@ExtractPayload Long hostId,
                                      @PathVariable Long studyId) {
        studyService.close(studyId);
        return ResponseEntity.noContent().build();
    }
}
