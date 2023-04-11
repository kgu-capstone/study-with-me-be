package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdate;
import com.kgu.studywithme.study.service.StudyService;
import com.kgu.studywithme.study.service.StudyUpdateService;
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
    private final StudyUpdateService studyUpdateService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid StudyRegisterRequest request, @ExtractPayload Long hostId) {
        Long savedStudyId = studyService.register(request, hostId);
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/studies/{id}").build(savedStudyId))
                .build();
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyUpdate> getUpdateForm(@PathVariable Long studyId, @ExtractPayload Long hostId) {
        StudyUpdate response = studyUpdateService.getUpdateForm(studyId, hostId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{studyId}")
    public ResponseEntity<Void> update(@PathVariable Long studyId,
                                       @RequestBody @Valid StudyUpdate request,
                                       @ExtractPayload Long hostId) {
        studyUpdateService.update(studyId, request, hostId);
        return ResponseEntity.noContent().build();
    }
}
