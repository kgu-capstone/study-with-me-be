package com.kgu.studywithme.global.config;

import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.global.annotation.ExtractPayloadArgumentResolver;
import com.kgu.studywithme.global.annotation.ExtractTokenArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdditionalWebConfiguration implements WebMvcConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtractTokenArgumentResolver(jwtTokenProvider));
        resolvers.add(new ExtractPayloadArgumentResolver(jwtTokenProvider));
    }
}
