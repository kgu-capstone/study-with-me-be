package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.controller.dto.request.LoginRequest;
import com.kgu.studywithme.auth.controller.utils.LoginRequestUtils;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth [Controller Layer] -> AuthApiController 테스트")
class AuthApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("로그인 API 테스트 [POST /api/login]")
    class login {
        private static final String BASE_URL = "/api/login";

        @Test
        @DisplayName("이메일에 해당하는 사용자가 없으면 로그인에 실패한다")
        void wrongEmail() throws Exception {
            // given
            final LoginRequest request = LoginRequestUtils.createRequest("diff" + SEO_JI_WON.getEmail(), SEO_JI_WON.getPassword());
            given(authService.login(anyString(), anyString())).willThrow(StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.MEMBER_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "AuthApi/Login/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("email").description("로그인 이메일"),
                                            fieldWithPath("password").description("로그인 비밀번호")
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
        @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
        void wrongPassword() throws Exception {
            // given
            final LoginRequest request = LoginRequestUtils.createRequest(SEO_JI_WON.getEmail(), "diff" + SEO_JI_WON.getPassword());
            given(authService.login(anyString(), anyString())).willThrow(StudyWithMeException.type(MemberErrorCode.WRONG_PASSWORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.WRONG_PASSWORD;
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
                                    "AuthApi/Login/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("email").description("로그인 이메일"),
                                            fieldWithPath("password").description("로그인 비밀번호")
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
        @DisplayName("정보가 모두 일치하면 로그인에 성공하고 Access Token & Refresh Token을 발급받는다")
        void success() throws Exception {
            // given
            final LoginRequest request = LoginRequestUtils.createRequest(SEO_JI_WON.getEmail(), SEO_JI_WON.getPassword());
            final TokenResponse response = TokenResponse.builder()
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .build();
            given(authService.login(anyString(), anyString())).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN))
                    .andDo(
                            document(
                                    "AuthApi/Login/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("email").description("로그인 이메일"),
                                            fieldWithPath("password").description("로그인 비밀번호")
                                    ),
                                    responseFields(
                                            fieldWithPath("accessToken").description("발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("로그아웃 API 테스트 [POST /api/logout]")
    class logout {
        private static final String BASE_URL = "/api/logout";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 로그아웃에 실패한다")
        void withoutAccessToken() throws Exception {
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
                                    "AuthApi/Logout/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("로그아웃에 성공한다")
        void success() throws Exception {
            // when
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "AuthApi/Logout/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    )
                            )
                    );
        }
    }
}
