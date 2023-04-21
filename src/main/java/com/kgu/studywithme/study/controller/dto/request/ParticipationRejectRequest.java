package com.kgu.studywithme.study.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record ParticipationRejectRequest(
        @NotBlank(message = "참여 거절 사유는 필수입니다.")
        String reason
) {
}
