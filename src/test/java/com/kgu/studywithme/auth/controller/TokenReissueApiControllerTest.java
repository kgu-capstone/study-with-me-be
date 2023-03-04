package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth [Controller Layer] -> TokenReissueApiController 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TokenReissueApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("토큰 재발급 API 테스트 [POST /api/token/reissue]")
    class reissueTokens {
        private static final String BASE_URL = "/api/token/reissue";

        @Test
        void Authorization_Header에_RefreshToken이_없으면_예외가_발생한다() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "TokenReissueApi/Failure/Case1",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        void 만료된_RefreshToken으로_인해_토큰_재발급에_실패한다() throws Exception {
            // given
            given(jwtTokenProvider.getId(anyString())).willThrow(StudyWithMeException.type(AuthErrorCode.AUTH_EXPIRED_TOKEN));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + REFRESH_TOKEN);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.AUTH_EXPIRED_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "TokenReissueApi/Failure/Case2",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        void 이미_사용한_RefresToken이거나_조작된_RefreshToken이면_재발급에_실패한다() throws Exception {
            // given
            given(jwtTokenProvider.getId(anyString())).willThrow(StudyWithMeException.type(AuthErrorCode.AUTH_INVALID_TOKEN));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + REFRESH_TOKEN);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.AUTH_INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "TokenReissueApi/Failure/Case3",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        void RefresToken으로_AccessToken과_RefreshToken을_재발급받는다() throws Exception {
            // given
            given(jwtTokenProvider.getId(REFRESH_TOKEN)).willReturn(1L);

            TokenResponse response = TokenResponse.builder()
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .build();
            given(tokenReissueService.reissueTokens(1L, REFRESH_TOKEN)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + REFRESH_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN))
                    .andDo(
                            document(
                                    "TokenReissueApi/Success",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("accessToken").description("새로 발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("새로 발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }
}