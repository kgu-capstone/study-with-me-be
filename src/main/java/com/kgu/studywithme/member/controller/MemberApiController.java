package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import com.kgu.studywithme.member.service.MemberSignUpService;
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
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberSignUpService memberSignupService;

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        Long savedMemberId = memberSignupService.signUp(request.toEntity(), request.toInterestCategories());
        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/members/{id}").build(savedMemberId))
                .build();
    }
}
