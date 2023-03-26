package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.service.StudyRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyApiController {
    private final StudyRegisterService studyRegisterService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid StudyRegisterRequest request, @ExtractPayload Long hostId) {
        Long savedStudyId = studyRegisterService.register(request, hostId);
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/studies/{id}").build(savedStudyId))
                .build();
    }
}
