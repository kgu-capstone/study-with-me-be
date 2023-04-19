package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infra.oauth.OAuthProperties;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.service.dto.response.LoginResponse;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;
import java.util.UUID;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth [Controller Layer] -> OAuthApiController 테스트")
class OAuthApiControllerTest extends ControllerTest {
    @MockBean
    private OAuthProperties properties;

    @BeforeEach
    void setUp() {
        given(properties.getAuthUrl()).willReturn("https://accounts.google.com/o/oauth2/v2/auth");
        given(properties.getClientId()).willReturn("client_id");
        given(properties.getScope()).willReturn(Set.of("openid", "profile", "email"));
    }

    @Nested
    @DisplayName("Google OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access]")
    class getAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access";
        private static final String REDIRECT_URL = "http://localhost:3000";

        @Test
        @DisplayName("Authorization Code 요청을 위한 URI를 생성한다")
        void success() throws Exception {
            // given
            String authorizationCodeRequestUri = generateAuthorizationCodeRequestUri(REDIRECT_URL);
            given(oAuthUri.generate(REDIRECT_URL)).willReturn(authorizationCodeRequestUri);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("redirectUrl", REDIRECT_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value(authorizationCodeRequestUri)
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Access",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("redirectUrl").description("Authorization Code와 함께 redirect될 URI")
                                    ),
                                    responseFields(
                                            fieldWithPath("result").description("Authorization Code 요청을 위한 URI")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("Google OAuth 인증 API [GET /api/oauth/login]")
    class oAuthLogin {
        private static final String BASE_URL = "/api/oauth/login";
        private static final String AUTHORIZATION_CODE = UUID.randomUUID().toString().replaceAll("-", "").repeat(2);
        private static final String REDIRECT_URL = "http://localhost:3000";

        @Test
        @DisplayName("Google 이메일에 해당하는 사용자가 DB에 존재하지 않을 경우 예외가 발생하고 추가정보 기입을 통해서 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() throws Exception {
            // given
            GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();
            given(oAuthService.login(AUTHORIZATION_CODE, REDIRECT_URL)).willThrow(new StudyWithMeOAuthException(googleUserResponse));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("authorizationCode", AUTHORIZATION_CODE)
                    .param("redirectUrl", REDIRECT_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.name").exists(),
                            jsonPath("$.name").value(googleUserResponse.name()),
                            jsonPath("$.email").exists(),
                            jsonPath("$.email").value(googleUserResponse.email()),
                            jsonPath("$.picture").exists(),
                            jsonPath("$.picture").value(googleUserResponse.picture())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("authorizationCode").description("Authorization Code"),
                                            parameterWithName("redirectUrl").description("redirectUrl")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUrl과 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("name").description("회원가입 진행 시 이름 기본값 [Read-Only]"),
                                            fieldWithPath("email").description("회원가입 진행 시 이메일 기본값 [Read-Only]"),
                                            fieldWithPath("picture").description("회원가입 진행 시 구글 프로필 이미지 기본값 [Read-Only]")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("Google 이메일에 해당하는 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() throws Exception {
            // given
            LoginResponse response = JIWON.toLoginResponse();
            given(oAuthService.login(AUTHORIZATION_CODE, REDIRECT_URL)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("authorizationCode", AUTHORIZATION_CODE)
                    .param("redirectUrl", REDIRECT_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.member.id").exists(),
                            jsonPath("$.member.id").value(1L),
                            jsonPath("$.member.nickname").exists(),
                            jsonPath("$.member.nickname").value(JIWON.getNickname()),
                            jsonPath("$.member.email").exists(),
                            jsonPath("$.member.email").value(JIWON.getEmail()),
                            jsonPath("$.accessToken").exists(),
                            jsonPath("$.accessToken").value(response.accessToken()),
                            jsonPath("$.refreshToken").exists(),
                            jsonPath("$.refreshToken").value(response.refreshToken())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("authorizationCode").description("Authorization Code"),
                                            parameterWithName("redirectUrl").description("redirectUrl")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUrl과 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("member.id").description("사용자 ID(PK)"),
                                            fieldWithPath("member.nickname").description("사용자 닉네임"),
                                            fieldWithPath("member.email").description("사용자 이메일"),
                                            fieldWithPath("accessToken").description("발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/oauth/logout]")
    class logout {
        private static final String BASE_URL = "/api/oauth/logout";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 로그아웃에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Logout/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("로그아웃에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "OAuthApi/Logout/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    )
                            )
                    );
        }
    }

    private String generateAuthorizationCodeRequestUri(String redirectUrl) {
        return properties.getAuthUrl() + "?"
                + "response_type=code&"
                + "client_id=" + properties.getClientId() + "&"
                + "scope=" + String.join(" ", properties.getScope()) + "&"
                + "redirect_uri=" + redirectUrl;
    }
}
