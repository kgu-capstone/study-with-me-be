package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.controller.dto.request.LoginRequest;
import com.kgu.studywithme.auth.service.AuthService;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.global.annotation.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request.email(), request.password());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@ExtractPayload Long memberId) {
        authService.logout(memberId);
        return ResponseEntity.noContent().build();
    }
}
