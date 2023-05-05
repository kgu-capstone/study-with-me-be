package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.study.controller.utils.StudyRegisterRequestUtils.createOfflineStudyRegisterRequest;
import static com.kgu.studywithme.study.controller.utils.StudyRegisterRequestUtils.createOnlineStudyRegisterRequest;
import static com.kgu.studywithme.study.controller.utils.StudyUpdateRequestUtils.createOfflineStudyUpdateRequest;
import static com.kgu.studywithme.study.controller.utils.StudyUpdateRequestUtils.createOnlineStudyUpdateRequest;
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

@DisplayName("Study [Controller Layer] -> StudyApiController 테스트")
class StudyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 생성 API [POST /api/study]")
    class register {
        private static final String BASE_URL = "/api/study";
        private static final Long HOST_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 생성을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final StudyRegisterRequest request = createOnlineStudyRegisterRequest(Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
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
                                    "StudyApi/Register/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation").description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 해시태그 개수가 0개면 스터디를 생성할 수 없다 [최소 1개]")
        void throwExceptionByHashtagCountUnderflow() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            final StudyRegisterRequest request = createOnlineStudyRegisterRequest(Set.of());
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "스터디는 최소 1개의 해시태그를 가져야 합니다.";
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
                                    "StudyApi/Register/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation").description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 해시태그 개수가 5개를 초과하면 스터디를 생성할 수 없다 [최대 5개]")
        void throwExceptionByHashtagCountOverflow() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            final StudyRegisterRequest request = createOnlineStudyRegisterRequest(Set.of("A", "B", "C", "D", "E", "F"));
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "스터디는 최대 5개의 해시태그를 가질 수 있습니다.";
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
                                    "StudyApi/Register/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation").description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("중복되는 값(스터디 이름)에 의해서 스터디 생성을 실패한다")
        void throwExceptionByDuplicateName() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME))
                    .when(studyService)
                    .register(any(), any());

            // when
            final StudyRegisterRequest request = createOnlineStudyRegisterRequest(Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.DUPLICATE_NAME;
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
                                    "StudyApi/Register/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation").description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 생성에 성공한다 - 온라인")
        void successOnline() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            given(studyService.register(any(), any())).willReturn(1L);

            // when
            final StudyRegisterRequest request = createOnlineStudyRegisterRequest(Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
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
                                    "StudyApi/Register/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation").description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("스터디 생성에 성공한다 - 오프라인")
        void successOffline() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            given(studyService.register(any(), any())).willReturn(1L);

            // when
            final StudyRegisterRequest request = createOfflineStudyRegisterRequest(Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
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
                                    "StudyApi/Register/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation").description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 정보 수정 API [PATCH /api/studies/{studyId}]")
    class update {
        private static final String BASE_URL = "/api/studies/{studyId}";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 정보 수정을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Update/Failure/Case1",
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
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장이 아니라면 정보를 수정할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Update/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 해시태그 개수가 0개면 스터디 정보를 수정할 수 없다 [최소 1개]")
        void throwExceptionByHashtagCountUnderflow() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "스터디는 최소 1개의 해시태그를 가져야 합니다.";
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
                                    "StudyApi/Update/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 해시태그 개수가 5개를 초과하면 스터디 정보를 수정할 수 없다 [최대 5개]")
        void throwExceptionByHashtagCountOverflow() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of("A", "B", "C", "D", "E", "F"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "스터디는 최대 5개의 해시태그를 가질 수 있습니다.";
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
                                    "StudyApi/Update/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("다른 스터디가 사용하고 있는 스터디명이라면 정보를 수정할 수 없다")
        void throwExceptionByDuplicateName() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME))
                    .when(studyService)
                    .update(any(), any(), any());

            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.DUPLICATE_NAME;
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
                                    "StudyApi/Update/Failure/Case5",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("최대 수용인원을 현재 스터디 인원보다 적게 설정할 수 없다")
        void throwExceptionByCapacityCannotBeLessThanParticipants() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_PARTICIPANTS))
                    .when(studyService)
                    .update(any(), any(), any());

            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_PARTICIPANTS;
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
                                    "StudyApi/Update/Failure/Case6",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("정보 수정에 성공한다 - 온라인")
        void successOnline() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(studyService)
                    .update(any(), any(), any());

            // when
            final StudyUpdateRequest request = createOnlineStudyUpdateRequest(5, Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Update/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("정보 수정에 성공한다 - 오프라인")
        void successOffline() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doNothing()
                    .when(studyService)
                    .update(any(), any(), any());

            // when
            final StudyUpdateRequest request = createOfflineStudyUpdateRequest(5, Set.of("A", "B", "C"));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Update/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수정할 스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("capacity").description("최대 수용 인원"),
                                            fieldWithPath("category").description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("type").description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = on or ON / 오프라인 = off or OFF")),
                                            fieldWithPath("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("hashtags").description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 삭제 API [DELETE /api/studies/{studyId}]")
    class close {
        private static final String BASE_URL = "/api/studies/{studyId}";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디를 종료할 수 없다")
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
                                    "StudyApi/Close/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("종료할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 팀장이 아니라면 스터디를 종료할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Close/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("종료할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디를 종료한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Close/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("종료할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
