package com.kgu.studywithme.global.exception;

import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthUserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyWithMeOAuthException extends RuntimeException {
    private OAuthUserResponse response;

    public StudyWithMeOAuthException(OAuthUserResponse response) {
        super();
        this.response = response;
    }
}
