package com.kgu.studywithme.auth.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@DisplayName("Auth [Utils] -> AuthorizationExtractor 테스트")
class AuthorizationExtractorTest {
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }
    
    @Test
    @DisplayName("HTTP Request Message의 Authorization Header에 토큰이 없다면 Optional 빈 값을 응답한다")
    void emptyToken() {
        // given
        given(request.getHeader(AUTHORIZATION)).willReturn(null);
        
        // when
        Optional<String> token = AuthorizationExtractor.extractToken(request);

        // then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("HTTP Request Message의 Authorization Header에 토큰 타입만 명시되었다면 Optional 빈 값을 응답한다")
    void emptyTokenWithType() {
        // given
        given(request.getHeader(AUTHORIZATION)).willReturn(BEARER_TOKEN);

        // when
        Optional<String> token = AuthorizationExtractor.extractToken(request);

        // then
        assertThat(token).isEmpty();
    }
    
    @Test
    @DisplayName("HTTP Request Message의 Authorization Header에 토큰이 있다면 Optional로 감싸서 응답한다")
    void tokenExists() {
        // given
        given(request.getHeader(AUTHORIZATION)).willReturn(String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

        // when
        Optional<String> token = AuthorizationExtractor.extractToken(request);

        // then
        assertThat(token).isPresent();
        assertThat(token.get()).isEqualTo(ACCESS_TOKEN);
    }
}
