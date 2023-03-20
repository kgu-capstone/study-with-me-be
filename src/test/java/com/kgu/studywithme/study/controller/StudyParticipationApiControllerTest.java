package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> StudyParticipationApiController 테스트")
class StudyParticipationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 참여 신청 API [POST /api/studies/{studyId}/applicants]")
    class apply {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 참여 신청에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID);

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
                                    "StudyApi/Participation/Apply/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
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
        @DisplayName("모집이 마감된 스터디에는 참여 신청을 할 수 없다")
        void failureByRecruitmentCompleted() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.RECRUITMENT_IS_COMPLETE))
                    .when(participationService)
                    .apply(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.RECRUITMENT_IS_COMPLETE;
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
                                    "StudyApi/Participation/Apply/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
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
        @DisplayName("스터디 팀장은 참여 신청을 할 수 없다")
        void failureByHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_HOST))
                    .when(participationService)
                    .apply(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_HOST;
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
                                    "StudyApi/Participation/Apply/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
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
        @DisplayName("이미 참여 신청을 했거나 참여중이라면 중복으로 참여 신청을 할 수 없다")
        void failureByAlreadyApply() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_PARTICIPANT))
                    .when(participationService)
                    .apply(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_PARTICIPANT;
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
                                    "StudyApi/Participation/Apply/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
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
        @DisplayName("참여 신청에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doNothing()
                    .when(participationService)
                    .apply(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNoContent(),
                            jsonPath("$").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }
}