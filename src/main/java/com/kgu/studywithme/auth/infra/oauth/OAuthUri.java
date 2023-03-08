package com.kgu.studywithme.auth.infra.oauth;

@FunctionalInterface
public interface OAuthUri {
    String generate(String redirectUri);
}
