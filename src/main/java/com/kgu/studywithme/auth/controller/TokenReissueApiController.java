package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.service.TokenReissueService;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.auth.utils.ExtractToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final TokenReissueService tokenReissueService;

    @PostMapping
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractPayload Long memberId, @ExtractToken String refreshToken) {
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(memberId, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
