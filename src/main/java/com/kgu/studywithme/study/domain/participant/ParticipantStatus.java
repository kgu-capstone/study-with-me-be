package com.kgu.studywithme.study.domain.participant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipantStatus {
    APPLY("참여 신청"),
    APPROVE("참여 승인"),
    REJECT("참여 거부"),
    CALCEL("참여 취소"),
    GRADUATED("졸업"),
    ;

    private final String description;
}
