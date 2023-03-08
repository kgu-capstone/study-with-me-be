package com.kgu.studywithme.auth.controller.dto.request;

import com.kgu.studywithme.auth.exception.AuthRequestValidationMessage;
import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank(message = AuthRequestValidationMessage.OAuthLogin.CODE)
        String code,

        @NotBlank(message = AuthRequestValidationMessage.OAuthLogin.REDIRECT_URL)
        String redirectUrl
) {
    @Builder
    public OAuthLoginRequest {}
}
