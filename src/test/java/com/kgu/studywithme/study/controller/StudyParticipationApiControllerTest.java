package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.controller.dto.request.ParticipationRejectRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.fixture.MemberFixture.DUMMY2;
import static org.mockito.ArgumentMatchers.*;
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

@DisplayName("Study [Controller Layer] -> StudyParticipationApiController 테스트")
class StudyParticipationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 참여 신청 API [POST /api/studies/{studyId}/applicants]")
    class apply {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long MEMBER_ID = 2L;
        private static final Long PARTICIPANT_ID = 3L;

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
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("모집이 마감된 스터디에는 참여 신청을 할 수 없다")
        void throwExceptionByRecruitmentIsComplete() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
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
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장은 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
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
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("이미 참여 신청을 했거나 참여중이라면 중복으로 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
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
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("해당 스터디에 대해서 졸업 또는 참여 취소 이력이 존재한다면 다시 참여 신청을 할 수 없다")
        void throwExceptionByMemberIsAlreadyGraduateOrCancel() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_ALREADY_GRADUATE_OR_CANCEL))
                    .when(participationService)
                    .apply(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_ALREADY_GRADUATE_OR_CANCEL;
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
                                    "StudyApi/Participation/Apply/Failure/Case5",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 신청에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doNothing()
                    .when(participationService)
                    .apply(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 진행할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여 신청 취소 API [DELETE /api/studies/{studyId}/applicants]")
    class applyCancel {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;
        private static final Long APPLIER_ID = 1L;
        private static final Long ANONYMOUS_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 참여 신청 취소에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID);

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
                                    "StudyApi/Participation/ApplyCancel/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 취소할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 신청자가 아니면 참여 신청을 취소할 수 없다")
        void throwExceptionByMemberIsNotApplier() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_APPLIER))
                    .when(participationService)
                    .applyCancel(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_APPLIER;
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
                                    "StudyApi/Participation/ApplyCancel/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 취소할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 신청 취소에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(APPLIER_ID);
            doNothing()
                    .when(participationService)
                    .applyCancel(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/ApplyCancel/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("참여 신청을 취소할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여 승인 API [PATCH /api/studies/{studyId}/applicants/{applierId}/approve]")
    class approve {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants/{applierId}/approve";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long APPLIER_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, APPLIER_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 참여 승인에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID);

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
                                    "StudyApi/Participation/Approve/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장이 아니면 참여 승인 권한이 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(APPLIER_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
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
                                    "StudyApi/Participation/Approve/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 승인을 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(participationService)
                    .approve(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ALREADY_CLOSED;
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
                                    "StudyApi/Participation/Approve/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 신청자가 아니면 참여 승인을 할 수 없다")
        void throwExceptionByMemberIsNotApplier() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_APPLIER))
                    .when(participationService)
                    .approve(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_APPLIER;
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
                                    "StudyApi/Participation/Approve/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 인원이 꽉 찼다면 더이상 참여 승인을 할 수 없다")
        void throwExceptionByStudyCapacityIsFull() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.STUDY_CAPACITY_IS_FULL))
                    .when(participationService)
                    .approve(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.STUDY_CAPACITY_IS_FULL;
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
                                    "StudyApi/Participation/Approve/Failure/Case5",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 승인에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(participationService)
                    .approve(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Approve/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절 API [PATCH /api/studies/{studyId}/applicants/{applierId}/reject]")
    class reject {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants/{applierId}/reject";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long APPLIER_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, APPLIER_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 참여 거절에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final ParticipationRejectRequest request = createParticipationRejectRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
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
                                    "StudyApi/Participation/Reject/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장이 아니면 참여 거절 권한이 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(APPLIER_ID);

            // when
            final ParticipationRejectRequest request = createParticipationRejectRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
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
                                    "StudyApi/Participation/Reject/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("거절 사유를 적지 않으면 참여 신청을 거절할 수 없다")
        void throwExceptionByRejectReasonIsEmpty() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(participationService)
                    .reject(any(), any(), any(), any());

            // when
            final ParticipationRejectRequest request = new ParticipationRejectRequest("");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "참여 거절 사유는 필수입니다.";
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(message)
                    )
                    .andDo(
                            document(
                                    "StudyApi/Participation/Reject/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 거절을 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(participationService)
                    .reject(any(), any(), any(), any());

            // when
            final ParticipationRejectRequest request = createParticipationRejectRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ALREADY_CLOSED;
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
                                    "StudyApi/Participation/Reject/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 신청자가 아니면 참여 거절을 할 수 없다")
        void throwExceptionByMemberIsNotApplier() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_APPLIER))
                    .when(participationService)
                    .reject(any(), any(), any(), any());

            // when
            final ParticipationRejectRequest request = createParticipationRejectRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_APPLIER;
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
                                    "StudyApi/Participation/Reject/Failure/Case5",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 거절을 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(participationService)
                    .reject(any(), any(), any(), any());

            // when
            final ParticipationRejectRequest request = createParticipationRejectRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Reject/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여 취소 API [PATCH /api/studies/{studyId}/participants/cancel]")
    class cancel {
        private static final String BASE_URL = "/api/studies/{studyId}/participants/cancel";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;

        @BeforeEach
        void setUp() {
            Study study = createSpringStudy(HOST_ID, STUDY_ID);
            mockingForStudyParticipant(study, DUMMY1, PARTICIPANT_ID, true);
            mockingForStudyParticipant(study, DUMMY2, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 참여 취소에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID);

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
                                    "StudyApi/Participation/Cancel/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여자가 아니면 참여 취소를 할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

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
                                    "StudyApi/Participation/Cancel/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료되었다면 더이상 참여 취소를 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(participationService)
                    .cancel(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ALREADY_CLOSED;
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
                                    "StudyApi/Participation/Cancel/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장은 참여 취소를 할 수 없다")
        void throwExceptionByMemberIsHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_HOST))
                    .when(participationService)
                    .cancel(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Participation/Cancel/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 취소에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doNothing()
                    .when(participationService)
                    .cancel(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Cancel/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 팀장 권한 위임 API [PATCH /api/studies/{studyId}/participants/{participantId}/delegation]")
    class delegateAuthority {
        private static final String BASE_URL = "/api/studies/{studyId}/participants/{participantId}/delegation";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, PARTICIPANT_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 팀장 권한 위임에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID);

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
                                    "StudyApi/Participation/DelegateAuthority/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("participantId").description("권한을 위임할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("팀장이 아니라면 팀장 권한을 위임할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
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
                                    "StudyApi/Participation/DelegateAuthority/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("participantId").description("권한을 위임할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료되었다면 팀장 권한을 위임할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(participationService)
                    .delegateAuthority(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ALREADY_CLOSED;
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
                                    "StudyApi/Participation/DelegateAuthority/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("participantId").description("권한을 위임할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여자가 아니면 팀장 권한을 위임할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT))
                    .when(participationService)
                    .delegateAuthority(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, ANONYMOUS_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

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
                                    "StudyApi/Participation/DelegateAuthority/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("participantId").description("권한을 위임할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("팀장 권한 위임에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(participationService)
                    .delegateAuthority(anyLong(), anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/DelegateAuthority/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("participantId").description("권한을 위임할 사용자 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 졸업 API [PATCH /api/studies/{studyId}/graduate]")
    class graduate {
        private static final String BASE_URL = "/api/studies/{studyId}/graduate";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;

        @BeforeEach
        void setUp() {
            Study study = createSpringStudy(HOST_ID, STUDY_ID);
            mockingForStudyParticipant(study, DUMMY1, PARTICIPANT_ID, true);
            mockingForStudyParticipant(study, DUMMY2, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 졸업에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID);

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
                                    "StudyApi/Participation/Graduate/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("졸업할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여자가 아니면 졸업을 할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

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
                                    "StudyApi/Participation/Graduate/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("졸업할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료되었다면 졸업을 할 수 없다")
        void throwExceptionByStudyIsAlreadyClosed() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(participationService)
                    .graduate(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ALREADY_CLOSED;
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
                                    "StudyApi/Participation/Graduate/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("졸업할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장은 졸업이 불가능하고 졸업을 하기 위해서는 팀장 권한을 위임해야 한다")
        void throwExceptionByMemberIsHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_HOST))
                    .when(participationService)
                    .graduate(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Participation/Graduate/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("졸업할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("졸업에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);
            doNothing()
                    .when(participationService)
                    .graduate(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Graduate/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("졸업할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private ParticipationRejectRequest createParticipationRejectRequest() {
        return new ParticipationRejectRequest("나이가 너무 많아요");
    }
}
