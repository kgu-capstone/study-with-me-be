package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.doNothing;
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

@DisplayName("Study [Controller Layer] -> StudyNoticeApiController 테스트")
class StudyNoticeApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("공지사항 등록 API [POST /api/studies/{studyId}/notice]")
    class register {
        private static final String BASE_URL = "/api/studies/{studyId}/notice";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 공지사항 등록을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final NoticeRequest request = createNoticeRequest();
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
                                    "StudyApi/Notice/Register/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("공지사항 제목"),
                                            fieldWithPath("content").description("공지사항 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("팀장이 아니라면 공지사항을 등록할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            final NoticeRequest request = createNoticeRequest();
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
                                    "StudyApi/Notice/Register/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 등록할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("공지사항 제목"),
                                            fieldWithPath("content").description("공지사항 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항 등록에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            given(noticeService.register(any(), anyString(), anyString())).willReturn(1L);

            // when
            final NoticeRequest request = createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Notice/Register/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
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
    }

    @Nested
    @DisplayName("공지사항 삭제 API [DELETE /api/studies/{studyId}/notices/{noticeId}]")
    class remove {
        private static final String BASE_URL = "/api/studies/{studyId}/notices/{noticeId}";
        private static final Long STUDY_ID = 1L;
        private static final Long NOTICE_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 공지사항 삭제를 실패한다")
        void withoutAccessToken() throws Exception {
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
                                    "StudyApi/Notice/Remove/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 삭제할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("팀장이 아니라면 공지사항을 삭제할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

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
                                    "StudyApi/Notice/Remove/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 삭제할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("작성자가 아니라면 공지사항을 삭제할 수 없다")
        void throwExceptionByMemberIsNotWriter() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER))
                    .when(noticeService)
                    .remove(any(), any());

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
                                    "StudyApi/Notice/Remove/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 삭제할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항 삭제에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(noticeService)
                    .remove(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, NOTICE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Notice/Remove/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 삭제할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("삭제할 공지사항 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("공지사항 수정 API [PATCH /api/studies/{studyId}/notices/{noticeId}]")
    class update {
        private static final String BASE_URL = "/api/studies/{studyId}/notices/{noticeId}";
        private static final Long STUDY_ID = 1L;
        private static final Long NOTICE_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 공지사항 수정에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final NoticeRequest request = createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, NOTICE_ID)
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
                                    "StudyApi/Notice/Update/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 수정할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("수정할 공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 공지사항 제목"),
                                            fieldWithPath("content").description("수정할 공지사항 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("팀장이 아니라면 공지사항을 수정할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            final NoticeRequest request = createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, NOTICE_ID)
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
                                    "StudyApi/Notice/Update/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 수정할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("수정할 공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 공지사항 제목"),
                                            fieldWithPath("content").description("수정할 공지사항 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("작성자가 아니라면 공지사항을 수정할 수 없다")
        void throwExceptionByMemberIsNotWriter() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER))
                    .when(noticeService)
                    .update(any(), any(), any(), any());

            // when
            final NoticeRequest request = createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, NOTICE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

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
                                    "StudyApi/Notice/Update/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 수정할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("수정할 공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 공지사항 제목"),
                                            fieldWithPath("content").description("수정할 공지사항 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항 수정에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(noticeService)
                    .update(any(), any(), any(), any());

            // when
            final NoticeRequest request = createNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, NOTICE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Notice/Update/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("공지사항 수정할 스터디 ID(PK)"),
                                            parameterWithName("noticeId").description("수정할 공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 공지사항 제목"),
                                            fieldWithPath("content").description("수정할 공지사항 내용")
                                    )
                            )
                    );
        }
    }

    private NoticeRequest createNoticeRequest() {
        return new NoticeRequest(
                "4/3 공지사항입니다.",
                "4/7 사당역 스터디룸에서 5시까지 모여주세요."
        );
    }
}
