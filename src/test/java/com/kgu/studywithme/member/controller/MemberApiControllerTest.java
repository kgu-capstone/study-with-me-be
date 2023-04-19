package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.controller.dto.request.MemberReportRequest;
import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberApiController 테스트")
class MemberApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원가입 API [POST /api/member]")
    class signUp {
        private static final String BASE_URL = "/api/member";

        @Test
        @DisplayName("중복되는 값(닉네임)에 의해서 회원가입에 실패한다")
        void throwExceptionByDuplicateNickname() throws Exception {
            // given
            final SignUpRequest request = createSignUpRequest();
            given(memberService.signUp(any())).willThrow(StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("email").description("이메일")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("birth").description("생년월일"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("gender").description("성별")
                                                    .attributes(constraint("M / F")),
                                            fieldWithPath("province").description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city").description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("categories").description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("회원가입에 성공한다")
        void success() throws Exception {
            // given
            final SignUpRequest request = createSignUpRequest();
            given(memberService.signUp(any())).willReturn(1L);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("email").description("이메일")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("birth").description("생년월일"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("gender").description("성별")
                                                    .attributes(constraint("M / F")),
                                            fieldWithPath("province").description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city").description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("categories").description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 신고 API [POST /api/members/{reporteeId}/report]")
    class report {
        private static final String BASE_URL = "/api/members/{reporteeId}/report";
        private static final Long REPORTEE_ID = 1L;
        private static final Long REPORTER_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 사용자 신고를 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REPORTEE_ID);

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
                                    "MemberApi/Report/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("reporteeId").description("신고 대상자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
        void throwExceptionByPreviousReportIsStillPending() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(REPORTER_ID);
            doThrow(StudyWithMeException.type(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING))
                    .when(memberService)
                    .report(anyLong(), anyLong(), anyString());

            // when
            final MemberReportRequest request = createReportRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REPORTEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/Report/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("reporteeId").description("신고 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("신고 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자 신고에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(REPORTER_ID);
            given(memberService.report(any(), any(), any())).willReturn(1L);

            // when
            final MemberReportRequest request = createReportRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REPORTEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Report/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("reporteeId").description("신고 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("신고 사유")
                                    )
                            )
                    );
        }
    }

    private SignUpRequest createSignUpRequest() {
        return SignUpRequest.builder()
                .name(JIWON.getName())
                .nickname(JIWON.getNickname())
                .email(JIWON.getEmail())
                .birth(JIWON.getBirth())
                .phone("01012345678")
                .gender("M")
                .province(JIWON.getProvince())
                .city(JIWON.getCity())
                .categories(List.of(LANGUAGE.getId(), INTERVIEW.getId(), PROGRAMMING.getId()))
                .build();
    }

    private MemberReportRequest createReportRequest() {
        return new MemberReportRequest("참여를 안해요");
    }
}
