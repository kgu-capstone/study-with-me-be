package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyParticipationApiController {
    private final ParticipationService participationService;

    @PostMapping("/applicants")
    public ResponseEntity<Void> apply(@PathVariable Long studyId, @ExtractPayload Long memberId) {
        participationService.apply(studyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
