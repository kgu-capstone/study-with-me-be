package com.kgu.studywithme.favorite.controller;

import com.kgu.studywithme.favorite.service.FavoriteManageService;
import com.kgu.studywithme.global.annotation.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/like")
public class FavoriteApiController {
    private final FavoriteManageService favoriteManageService;

    @PostMapping
    public ResponseEntity<Void> like(@PathVariable Long studyId, @ExtractPayload Long memberId) {
        favoriteManageService.like(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancel(@PathVariable Long studyId, @ExtractPayload Long memberId) {
        favoriteManageService.cancel(studyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
