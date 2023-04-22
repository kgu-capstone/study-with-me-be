package com.kgu.studywithme.favorite.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.favorite.service.FavoriteManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/like")
public class FavoriteApiController {
    private final FavoriteManageService favoriteManageService;

    @PostMapping
    public ResponseEntity<Void> like(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        favoriteManageService.like(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancel(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        favoriteManageService.cancel(studyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
