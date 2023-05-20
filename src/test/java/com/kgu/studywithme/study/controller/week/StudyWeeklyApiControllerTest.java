package com.kgu.studywithme.study.controller.week;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.study.controller.utils.StudyWeeklyRequestUtils.createWeekWithAssignmentRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> StudyWeeklyApiController 테스트")
class StudyWeeklyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 주차 생성 API [POST /api/studies/{studyId}/week] - AccessToken 필수")
    class createWeek {
        private static final String BASE_URL = "/api/studies/{studyId}/week";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        private MultipartFile files1;
        private MultipartFile files2;
        private MultipartFile files3;
        private MultipartFile files4;
        private List<MultipartFile> files;
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);

            files1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
            files2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
            files3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
            files4 = createMultipleMockMultipartFile("hello4.png", "image/png");
            files = List.of(files1, files2, files3, files4);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 주차를 생성할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            final StudyWeeklyRequest request = createWeekWithAssignmentRequest(STUDY_WEEKLY_1, files, true);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("title", request.title())
                    .param("content", request.content())
                    .param("startDate", request.startDate().format(DATE_TIME_FORMATTER))
                    .param("endDate", request.endDate().format(DATE_TIME_FORMATTER))
                    .param("assignmentExists", String.valueOf(request.assignmentExists()))
                    .param("autoAttendance", String.valueOf(request.autoAttendance()));

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
                                    "StudyApi/Weekly/Create/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("이미 해당 주차가 등록되었다면 중복으로 등록할 수 없다")
        void throwExceptionByAlreadyWeekCreated() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_WEEK_CREATED))
                    .when(studyWeeklyService)
                    .createWeek(any(), any());

            // when
            final StudyWeeklyRequest request = createWeekWithAssignmentRequest(STUDY_WEEKLY_1, files, true);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("title", request.title())
                    .param("content", request.content())
                    .param("startDate", request.startDate().format(DATE_TIME_FORMATTER))
                    .param("endDate", request.endDate().format(DATE_TIME_FORMATTER))
                    .param("assignmentExists", String.valueOf(request.assignmentExists()))
                    .param("autoAttendance", String.valueOf(request.autoAttendance()));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ALREADY_WEEK_CREATED;
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
                                    "StudyApi/Weekly/Create/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차를 생성한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            final StudyWeeklyRequest request = createWeekWithAssignmentRequest(STUDY_WEEKLY_1, files, true);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("title", request.title())
                    .param("content", request.content())
                    .param("startDate", request.startDate().format(DATE_TIME_FORMATTER))
                    .param("endDate", request.endDate().format(DATE_TIME_FORMATTER))
                    .param("assignmentExists", String.valueOf(request.assignmentExists()))
                    .param("autoAttendance", String.valueOf(request.autoAttendance()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Create/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차 삭제 API [DELETE /api/studies/{studyId}/weeks/{week}] - AccessToken 필수")
    class deleteWeek {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{week}";
        private static final Integer WEEK = 1;
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 주차를 삭제할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEK)
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
                                    "StudyApi/Weekly/Delete/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("삭제할 주차")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("가장 최신 주차가 아님에 따라 주차를 삭제할 수 없다")
        void throwExceptionByWeekIsNotLatest() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.WEEK_IS_NOT_LATEST))
                    .when(studyWeeklyService)
                    .deleteWeek(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.WEEK_IS_NOT_LATEST;
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
                                    "StudyApi/Weekly/Delete/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("삭제할 주차")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차를 삭제한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Delete/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("삭제할 주차")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 과제 제출 API [POST /api/studies/{studyId}/weeks/{week}/assignment] - AccessToken 필수")
    class submitAssignment {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{week}/assignment";
        private static final Integer WEEK = 1;
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private MultipartFile file;

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);

            file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 스터디 주차별 과제를 제출할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "file");

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
                                    "StudyApi/Weekly/SubmitAssignment/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("과제를 제출할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("과제 제출물을 업로드 하지 않으면 예외가 발생한다")
        void throwExceptionByMissingSubmission() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MISSING_SUBMISSION))
                    .when(studyWeeklyService)
                    .submitAssignment(any(), any(), any(), any(), any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "file");

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MISSING_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/SubmitAssignment/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("과제를 제출할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("링크 + 파일을 둘다 업로드하면 예외가 발생한다")
        void throwExceptionByDuplicateSubmission() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_SUBMISSION))
                    .when(studyWeeklyService)
                    .submitAssignment(any(), any(), any(), any(), any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "file")
                    .param("link", "https://notion.so");

            // then
            final StudyErrorCode expectedError = StudyErrorCode.DUPLICATE_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/SubmitAssignment/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("과제를 제출할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차별 과제를 제출한다 - 파일")
        void successWithLink() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "file");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/SubmitAssignment/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("과제를 제출할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차별 과제를 제출한다 - 링크")
        void successWithFile() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "link")
                    .param("link", "https://notion.so");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/SubmitAssignment/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("과제를 제출할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 제출한 과제 수정 API [PATCH /api/studies/{studyId}/weeks/{week}/assignment/edit] - AccessToken 필수")
    class editSubmittedAssignment {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{week}/assignment/edit";
        private static final Integer WEEK = 1;
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private MultipartFile file;

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);

            file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 제출한 과제를 수정할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "link")
                    .param("link", "https://notion.so");

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
                                    "StudyApi/Weekly/EditSubmittedAssignment/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("제출한 과제를 수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("과제 제출물을 업로드 하지 않으면 예외가 발생한다")
        void throwExceptionByMissingSubmission() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.MISSING_SUBMISSION))
                    .when(studyWeeklyService)
                    .editSubmittedAssignment(any(), any(), any(), any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "file");

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MISSING_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/EditSubmittedAssignment/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("제출한 과제를 수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("링크 + 파일을 둘다 업로드하면 예외가 발생한다")
        void throwExceptionByDuplicateSubmission() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_SUBMISSION))
                    .when(studyWeeklyService)
                    .editSubmittedAssignment(any(), any(), any(), any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "file")
                    .param("link", "https://notion.so");

            // then
            final StudyErrorCode expectedError = StudyErrorCode.DUPLICATE_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/EditSubmittedAssignment/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("제출한 과제를 수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("제출한 과제를 수정한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("type", "link")
                    .param("link", "https://notion.so");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/EditSubmittedAssignment/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("제출한 과제를 수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("file").description("제출할 파일")
                                                    .optional()
                                    ),
                                    requestParameters(
                                            parameterWithName("type").description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link").description("제출할 링크")
                                                    .optional()
                                    )
                            )
                    );
        }
    }
}
