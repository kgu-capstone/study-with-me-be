package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.controller.utils.NoticeRequestUtils;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> NoticeController 테스트")
class NoticeControllerTest extends ControllerTest {
    @Nested
    @DisplayName("공지사항 등록 API [POST /api/study/{studyId}/notice/register")
    class register {
        private static final String BASE_URL = "/api/study/{studyId}/notice/register";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 공지사항 등록을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final NoticeRequest request = NoticeRequestUtils.createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

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
                                    "NoticeApi/Register/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("공지사항 제목"),
                                            fieldWithPath("content").description("공지사항 내용")
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
        @DisplayName("팀장이 아니라면 공지사항을 등록할 수 없다")
        void throwExceptionByMemberNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                    .when(noticeService)
                    .register(any(), any(), any());

            // when
            final NoticeRequest request = NoticeRequestUtils.createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
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
                                    "NoticeApi/Register/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("공지사항 제목"),
                                            fieldWithPath("content").description("공지사항 내용")
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
        @DisplayName("공지사항 등록에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doAnswer(invocation -> 1L)
                    .when(noticeService)
                    .register(any(), any(), any());

            // when
            final NoticeRequest request = NoticeRequestUtils.createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNoContent(),
                            jsonPath("$").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "NoticeApi/Register/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("공지사항 제목"),
                                            fieldWithPath("content").description("공지사항 내용")
                                    )
                            )
                    );
        }

        @Nested
        @DisplayName("공지사항 삭제 API [POST /api/study/{studyId}/notice/{noticeId}/remove")
        class remove {
            private static final String BASE_URL = "/api/study/{studyId}/notice/{noticeId}/remove";
            private static final Long STUDY_ID = 1L;
            private static final Long NOTICE_ID = 1L;

            @Test
            @DisplayName("Authorization Header에 AccessToken이 없으면 공지사항 삭제를 실패한다")
            void withoutAccessToken () throws Exception {
                // when
                MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .delete(BASE_URL, STUDY_ID, NOTICE_ID);

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
                                        "NoticeApi/Remove/Failure/Case1",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        pathParameters(
                                                parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)"),
                                                parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
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
            @DisplayName("팀장이 아니라면 공지사항을 삭제할 수 없다")
            void throwExceptionByMemberNotHost () throws Exception {
                // given
                given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
                given(jwtTokenProvider.getId(anyString())).willReturn(1L);
                doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                        .when(noticeService)
                        .remove(any(), any(), any());

                // when
                MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .delete(BASE_URL, STUDY_ID, NOTICE_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
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
                                        "NoticeApi/Remove/Failure/Case2",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        requestHeaders(
                                                headerWithName(AUTHORIZATION).description("Access Token")
                                        ),
                                        pathParameters(
                                                parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)"),
                                                parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
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
            @DisplayName("작성자가 아니라면 공지사항을 삭제할 수 없다")
            void throwExceptionByMemberNotWriter () throws Exception {
                // given
                given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
                given(jwtTokenProvider.getId(anyString())).willReturn(1L);
                doThrow(StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER))
                        .when(noticeService)
                        .remove(any(), any(), any());

                // when
                MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .delete(BASE_URL, STUDY_ID, NOTICE_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final MemberErrorCode expectedError = MemberErrorCode.MEMBER_IS_NOT_WRITER;
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
                                        "NoticeApi/Remove/Failure/Case3",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        requestHeaders(
                                                headerWithName(AUTHORIZATION).description("Access Token")
                                        ),
                                        pathParameters(
                                                parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)"),
                                                parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
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
            @DisplayName("공지사항 삭제에 성공한다")
            void success () throws Exception {
                // given
                given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
                given(jwtTokenProvider.getId(anyString())).willReturn(1L);
                doNothing()
                        .when(noticeService)
                        .remove(any(), any(), any());

                // when
                MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .delete(BASE_URL, STUDY_ID, NOTICE_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                mockMvc.perform(requestBuilder)
                        .andExpectAll(
                                status().isNoContent(),
                                jsonPath("$").doesNotExist()
                        )
                        .andDo(
                                document(
                                        "NoticeApi/Remove/Success",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        requestHeaders(
                                                headerWithName(AUTHORIZATION).description("Access Token")
                                        ),
                                        pathParameters(
                                                parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)"),
                                                parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
                                        )
                                )
                        );
            }
        }
    }
}