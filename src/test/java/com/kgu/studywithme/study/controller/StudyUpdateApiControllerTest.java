package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdate;
import com.kgu.studywithme.study.domain.RecruitmentStatus;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.StudyFixture.TOEFL;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;
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

@DisplayName("Study [Controller Layer] -> StudyUpdateApiController 테스트")
class StudyUpdateApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("수정 폼 제공 API [GET /api/studies/update/{studyId}]")
    class showUpdateForm {
        private static final String BASE_URL = "/api/studies/update/{studyId}";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 수정 폼 제공을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID);

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
                                    "StudyApi/Update/Get/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
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
        @DisplayName("스터디 팀장이 아니라면 스터디 수정 폼을 제공할 수 없습니다")
        void throwExceptionByMemberNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                    .when(studyUpdateService)
                    .getUpdateForm(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Update/Get/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
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
        @DisplayName("정보 수정 폼 제공에 성공한다 - 온라인")
        void successOnline() throws Exception {
            // given
            final StudyUpdate response = generateOnlineStudyUpdate();
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doReturn(response)
                    .when(studyUpdateService)
                    .getUpdateForm(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "StudyApi/Update/Get/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("스터디 카테고리"),
                                            fieldWithPath("capacity").description("스터디 최대 인원"),
                                            fieldWithPath("type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags[]").description("스터디 해시태그")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("정보 수정 폼 제공에 성공한다 - 오프라인")
        void successOffline() throws Exception {
            // given
            final StudyUpdate response = generateOfflineStudyUpdate();
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doReturn(response)
                    .when(studyUpdateService)
                    .getUpdateForm(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "StudyApi/Update/Get/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("스터디 카테고리"),
                                            fieldWithPath("capacity").description("스터디 최대 인원"),
                                            fieldWithPath("type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags[]").description("스터디 해시태그")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("정보 수정 API [PUT /api/studies/update/{studyId}]")
    class update {
        private static final String BASE_URL = "/api/studies/update/{studyId}";
        private static final String STUDY_ID = "1";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 정보 수정을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final StudyUpdate request = generateOfflineStudyUpdate();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Update/Put/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("type").description("온/오프라인 유무"),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags").description("해시태그")
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
        @DisplayName("스터디 팀장이 아니라면 정보를 수정할 수 없다")
        void throwExceptionByMemberNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                    .when(studyUpdateService)
                    .update(any(), any(), any());

            // when
            final StudyUpdate request = generateOfflineStudyUpdate();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Update/Put/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("type").description("온/오프라인 유무"),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags").description("해시태그")
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
        @DisplayName("최대 수용인원을 현재 스터디 인원보다 적게 설정할 수 없다")
        void throwExceptionByCapacityLessThanMembers() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS))
                    .when(studyUpdateService)
                    .update(any(), any(), any());

            // when
            final StudyUpdate request = generateOfflineStudyUpdate();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS;
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
                                    "StudyApi/Update/Put/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("type").description("온/오프라인 유무"),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags").description("해시태그")
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
        @DisplayName("정보 수정에 성공한다 - 온라인")
        void successOnline() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doNothing()
                    .when(studyUpdateService)
                    .update(any(), any(), any());

            // when
            final StudyUpdate request = generateOnlineStudyUpdate();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNoContent()
                    )
                    .andDo(
                            document(
                                    "StudyApi/Update/Put/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("type").description("온/오프라인 유무"),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags").description("해시태그")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("정보 수정에 성공한다 - 오프라인")
        void successOffline() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doNothing()
                    .when(studyUpdateService)
                    .update(any(), any(), any());

            // when
            final StudyUpdate request = generateOfflineStudyUpdate();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNoContent()
                    )
                    .andDo(
                            document(
                                    "StudyApi/Update/Put/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("type").description("온/오프라인 유무"),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("hashtags").description("해시태그")
                                    )
                            )
                    );
        }
    }

    public StudyUpdate generateOfflineStudyUpdate() {
        return StudyUpdate.builder()
                .name(TOSS_INTERVIEW.getName())
                .description(TOSS_INTERVIEW.getDescription())
                .capacity(TOSS_INTERVIEW.getCapacity())
                .category(TOSS_INTERVIEW.getCategory().getId())
                .type(TOSS_INTERVIEW.getType().getDescription())
                .province(TOSS_INTERVIEW.getArea().getProvince())
                .city(TOSS_INTERVIEW.getArea().getCity())
                .recruitmentStatus(RecruitmentStatus.COMPLETE.getDescription())
                .hashtags(TOSS_INTERVIEW.getHashtags())
                .build();
    }

    public StudyUpdate generateOnlineStudyUpdate() {
        return StudyUpdate.builder()
                .name(TOEFL.name())
                .description(TOEFL.getDescription())
                .capacity(TOEFL.getCapacity())
                .category(TOEFL.getCategory().getId())
                .type(TOEFL.getType().getDescription())
                .province(null)
                .city(null)
                .recruitmentStatus(RecruitmentStatus.COMPLETE.getDescription())
                .hashtags(TOEFL.getHashtags())
                .build();
    }
}
