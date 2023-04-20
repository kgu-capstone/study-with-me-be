package com.kgu.studywithme.study.controller.week;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
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
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.study.controller.utils.StudyWeeklyRequestUtils.createWeekWithAssignmentRequest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> StudyWeeklyApiController 테스트")
class StudyWeeklyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 주차 생성 API [POST /api/studies/{studyId}/weeks/{week}]")
    class createWeek {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{week}";
        private static final Integer WEEK = 1;
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
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 주차를 생성할 수 없다")
        void withoutAccessToken() throws Exception {
            // when
            final StudyWeeklyRequest request = createWeekWithAssignmentRequest(STUDY_WEEKLY_1, files, true);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .param("title", request.title())
                    .param("content", request.content())
                    .param("startDate", request.startDate().format(DATE_TIME_FORMATTER))
                    .param("endDate", request.endDate().format(DATE_TIME_FORMATTER))
                    .param("assignmentExists", String.valueOf(request.assignmentExists()))
                    .param("autoAttendance", String.valueOf(request.autoAttendance()));

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
                                    "StudyApi/Weekly/Create/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("생성할 주차")
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
        @DisplayName("팀장이 아니라면 스터디 주차를 생성할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            final StudyWeeklyRequest request = createWeekWithAssignmentRequest(STUDY_WEEKLY_1, files, true);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
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
                                    "StudyApi/Weekly/Create/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("생성할 주차")
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
                    .multipart(BASE_URL, STUDY_ID, WEEK)
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
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("생성할 주차")
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
}
