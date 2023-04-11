package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdate;
import com.kgu.studywithme.study.service.StudyUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/update/{studyId}")
public class StudyUpdateApiController {
    private final StudyUpdateService studyUpdateService;

    @GetMapping
    public ResponseEntity<StudyUpdate> getUpdateForm(@PathVariable Long studyId, @ExtractPayload Long hostId) {
        StudyUpdate response = studyUpdateService.getUpdateForm(studyId, hostId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> update(@PathVariable Long studyId,
                                       @RequestBody @Valid StudyUpdate request,
                                       @ExtractPayload Long hostId) {
        studyUpdateService.update(studyId, request, hostId);
        return ResponseEntity.noContent().build();
    }
}
