package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.controller.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.auth.infra.oauth.OAuthUri;
import com.kgu.studywithme.auth.service.OAuthService;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthApiController {
    private final OAuthUri oAuthUri;
    private final OAuthService oAuthService;

    @GetMapping(value = "/access", params = {"redirectUrl"})
    public ResponseEntity<SimpleResponseWrapper<String>> access(@RequestParam String redirectUrl) {
        String link = oAuthUri.generate(redirectUrl);
        return ResponseEntity.ok(new SimpleResponseWrapper<>(link));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid OAuthLoginRequest request) {
        TokenResponse response = oAuthService.login(request.code(), request.redirectUrl());
        return ResponseEntity.ok(response);
    }
}
