package com.kgu.studywithme.auth.controller.dto.request;

import com.kgu.studywithme.auth.exception.AuthRequestValidationMessage;
import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = AuthRequestValidationMessage.Login.EMAIL)
        String email,

        @NotBlank(message = AuthRequestValidationMessage.Login.PASSWORD)
        String password
) {
    @Builder
    public LoginRequest {}
}
