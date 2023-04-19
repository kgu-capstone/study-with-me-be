package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.global.annotation.CheckMemberIdentity;
import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.member.service.MemberInformationService;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
import com.kgu.studywithme.member.service.dto.response.PeerReviewAssembler;
import com.kgu.studywithme.member.service.dto.response.RelatedStudy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberInformationApiController {
    private final MemberInformationService memberInformationService;

    @CheckMemberIdentity
    @GetMapping
    public ResponseEntity<MemberInformation> getInformation(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        MemberInformation response = memberInformationService.getInformation(memberId);
        return ResponseEntity.ok(response);
    }

    @CheckMemberIdentity
    @GetMapping("/studies/participate")
    public ResponseEntity<RelatedStudy> getParticipateStudy(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        RelatedStudy response = memberInformationService.getParticipateStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @CheckMemberIdentity
    @GetMapping("/studies/graduated")
    public ResponseEntity<RelatedStudy> getGraduatedStudy(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        RelatedStudy response = memberInformationService.getGraduatedStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @CheckMemberIdentity
    @GetMapping("/studies/favorite")
    public ResponseEntity<RelatedStudy> getFavoriteStudy(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        RelatedStudy response = memberInformationService.getFavoriteStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reviews")
    public ResponseEntity<PeerReviewAssembler> getReviews(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        PeerReviewAssembler response = memberInformationService.getPeerReviews(memberId);
        return ResponseEntity.ok(response);
    }
}
