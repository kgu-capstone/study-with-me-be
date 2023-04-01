package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.study.service.StudyInformationService;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyInformationApiController {
    private final StudyInformationService studyInformationService;

    @GetMapping
    public ResponseEntity<StudyInformation> getInformation(@PathVariable Long studyId) {
        StudyInformation response = studyInformationService.getInformation(studyId);
        return ResponseEntity.ok(response);
    }
}
