package com.kgu.studywithme.auth.infra.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleOAuthUri implements OAuthUri {
    private final OAuthProperties properties;

    @Override
    public String generate(String redirectUri) {
        Map<String, String> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", properties.getClientId());
        params.put("scope", String.join(" ", properties.getScope()));
        params.put("redirect_uri", redirectUri);
        return properties.getAuthUrl() + "?" + concatParams(params);
    }

    private String concatParams(Map<String, String> params) {
        return params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
