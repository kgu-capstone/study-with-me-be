package com.kgu.studywithme.favorite.controller;

import com.kgu.studywithme.favorite.service.FavoriteEnrollService;
import com.kgu.studywithme.global.annotation.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteApiController {
    private final FavoriteEnrollService favoriteEnrollService;

    @PostMapping("/enroll")
    public ResponseEntity<Void> enroll(@RequestBody Map<String, Long> request, @ExtractPayload Long memberId) {
        Long favoriteStudyId = favoriteEnrollService.enroll(request.get("studyId"), memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unenroll")
    public ResponseEntity<Void> unenroll(@RequestBody Map<String, Long> request, @ExtractPayload Long memberId) {
        favoriteEnrollService.unenroll(request.get("studyId"), memberId);
        return ResponseEntity.ok().build();
    }
}
