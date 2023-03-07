package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.controller.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.auth.controller.utils.OAuthLoginRequestUtils;
import com.kgu.studywithme.auth.infra.oauth.OAuthProperties;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;
import java.util.UUID;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
    @DisplayName("Google OAuth Authorization Code 요청을 위한 URI 조회 API 테스트 [GET /api/oauth/access]")
    class getAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access";
        private static final String redirectUrl = "http://localhost:3000";
        
        @Test
        @DisplayName("Authorization Code 요청을 위한 URI를 생성한다")
        void success() throws Exception {
            // given
            String authorizationCodeRequestUri = generateAuthorizationCodeRequestUri(redirectUrl);
            given(oAuthUri.generate(redirectUrl)).willReturn(authorizationCodeRequestUri);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("redirectUrl", redirectUrl);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(authorizationCodeRequestUri))
                    .andDo(
                            document(
                                    "OAuthApi/Access",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
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
    @DisplayName("Google OAuth 인증 API 테스트 [POST /api/oauth/login]")
    class oAuthLogin {
        private static final String BASE_URL = "/api/oauth/login";
        private static final String authorizationCode = UUID.randomUUID().toString().replaceAll("-", "").repeat(2);
        private static final String redirectUrl = "http://localhost:3000";

        @Test
        @DisplayName("Google 이메일에 해당하는 사용자가 DB에 존재하지 않을 경우 예외가 발생하고 추가정보 기입을 통해서 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() throws Exception {
            // given
            GoogleUserResponse googleUserResponse = GoogleUserResponse.builder()
                    .name(SEO_JI_WON.getName())
                    .email(SEO_JI_WON.getEmail())
                    .picture("picture.png")
                    .build();
            given(oAuthService.login(authorizationCode, redirectUrl)).willThrow(new StudyWithMeOAuthException(googleUserResponse));

            // when
            final OAuthLoginRequest request = OAuthLoginRequestUtils.createRequest(authorizationCode, redirectUrl);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.name").value(googleUserResponse.name()))
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.email").value(googleUserResponse.email()))
                    .andExpect(jsonPath("$.picture").exists())
                    .andExpect(jsonPath("$.picture").value(googleUserResponse.picture()))
                    .andDo(
                            document(
                                    "OAuthApi/Login/Failure",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
                                    requestFields(
                                            fieldWithPath("code").description("Authorization Code"),
                                            fieldWithPath("redirectUrl").description("redirectUrl [Authorization Code 요청 시 redirectUrl과 반드시 동일한 값]")
                                    ),
                                    responseFields(
                                            fieldWithPath("name").description("회원가입 진행 시 이름 기본값 [Read-Only]"),
                                            fieldWithPath("email").description("회원가입 진행 시 이메일 기본값 [Read-Only]"),
                                            fieldWithPath("picture").description("회원가입 진행 시 사진 기본값")
                                    )
                            )
                    );
        }
        
        @Test
        @DisplayName("Google 이메일에 해당하는 사용자가 DB에 존재하면 로그인에 성공하고 토큰을 발급해준다")
        void success() throws Exception {
            // given
            TokenResponse response = TokenResponse.builder()
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .build();
            given(oAuthService.login(authorizationCode, redirectUrl)).willReturn(response);

            // when
            final OAuthLoginRequest request = OAuthLoginRequestUtils.createRequest(authorizationCode, redirectUrl);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.accessToken").value(response.accessToken()))
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.refreshToken").value(response.refreshToken()))
                    .andDo(
                            document(
                                    "OAuthApi/Login/Success",
                                    applyRequestPreprocessor(),
                                    applyResponsePreprocessor(),
                                    requestFields(
                                            fieldWithPath("code").description("Authorization Code"),
                                            fieldWithPath("redirectUrl").description("redirectUrl [Authorization Code 요청 시 redirectUrl과 반드시 동일한 값]")
                                    ),
                                    responseFields(
                                            fieldWithPath("accessToken").description("발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("발급된 Refresh Token (Expire - 2주)")
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