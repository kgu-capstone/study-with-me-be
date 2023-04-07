package com.kgu.studywithme.global.exception;

import com.kgu.studywithme.auth.infra.oauth.dto.response.OAuthUserResponse;
import lombok.Getter;

@Getter
public class StudyWithMeOAuthException extends RuntimeException {
    private final OAuthUserResponse response;

    public StudyWithMeOAuthException(OAuthUserResponse response) {
        super();
        this.response = response;
    }
}
