package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.controller.dto.request.NoticeCommentRequest;
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

@DisplayName("Study [Controller Layer] -> StudyNoticeCommentApiController 테스트")
class StudyNoticeCommentApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("공지사항 댓글 등록 API [POST /api/notices/{noticeId}/comment] - AccessToken 필수")
    class register {
        private static final String BASE_URL = "/api/notices/{noticeId}/comment";
        private static final Long NOTICE_ID = 1L;
        private static final Long PARTICIPANT_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @Test
        @DisplayName("스터디 참여자가 아니면 공지사항에 댓글을 등록할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT))
                    .when(commentService)
                    .register(any(), any(), any());

            // when
            final NoticeCommentRequest request = new NoticeCommentRequest("공지사항 댓글~~");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, NOTICE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT;
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
                                    "StudyApi/NoticeComment/Register/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId").description("공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("댓글 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 등록에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doNothing()
                    .when(commentService)
                    .register(any(), any(), any());

            // when
            final NoticeCommentRequest request = new NoticeCommentRequest("공지사항 댓글~~");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, NOTICE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/NoticeComment/Register/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId").description("공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("댓글 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 삭제 API [DELETE /api/notices/{noticeId}/comments/{commentId}] - AccessToken 필수")
    class remove {
        private static final String BASE_URL = "/api/notices/{noticeId}/comments/{commentId}";
        private static final Long NOTICE_ID = 1L;
        private static final Long COMMENT_ID = 1L;
        private static final Long PARTICIPANT_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @Test
        @DisplayName("작성자가 아니라면 댓글을 삭제할 수 없다")
        void throwExceptionByMemberIsNotWriter() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER))
                    .when(commentService)
                    .remove(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, NOTICE_ID, COMMENT_ID)
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
                                    "StudyApi/NoticeComment/Remove/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId").description("공지사항 ID(PK)"),
                                            parameterWithName("commentId").description("삭제할 댓글 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항에 등록한 댓글 삭제에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doNothing()
                    .when(commentService)
                    .remove(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, NOTICE_ID, COMMENT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/NoticeComment/Remove/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId").description("공지사항 ID(PK)"),
                                            parameterWithName("commentId").description("삭제할 댓글 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 수정 API [PUT /api/notices/{noticeId}/comments/{commentId}] - AccessToken 필수")
    class update {
        private static final String BASE_URL = "/api/notices/{noticeId}/comments/{commentId}";
        private static final Long NOTICE_ID = 1L;
        private static final Long COMMENT_ID = 1L;
        private static final Long PARTICIPANT_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @Test
        @DisplayName("작성자가 아니라면 댓글을 수정할 수 없다")
        void throwExceptionByMemberIsNotWriter() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(MemberErrorCode.MEMBER_IS_NOT_WRITER))
                    .when(commentService)
                    .update(any(), any(), any());

            // when
            final NoticeCommentRequest request = new NoticeCommentRequest("공지사항 댓글~~");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, NOTICE_ID, COMMENT_ID)
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
                                    "StudyApi/NoticeComment/Update/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId").description("공지사항 ID(PK)"),
                                            parameterWithName("commentId").description("수정할 댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("수정할 댓글 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 수정에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doNothing()
                    .when(commentService)
                    .update(any(), any(), any());

            // when
            final NoticeCommentRequest request = new NoticeCommentRequest("공지사항 댓글~~");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, NOTICE_ID, COMMENT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));
            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/NoticeComment/Update/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId").description("공지사항 ID(PK)"),
                                            parameterWithName("commentId").description("수정할 댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("수정할 댓글 내용")
                                    )
                            )
                    );
        }
    }
}
