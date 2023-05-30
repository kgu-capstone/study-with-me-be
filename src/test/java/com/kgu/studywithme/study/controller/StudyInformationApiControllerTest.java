package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.infra.query.dto.response.CommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;
import com.kgu.studywithme.study.infra.query.dto.response.StudyApplicantInformation;
import com.kgu.studywithme.study.service.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> StudyInformationApiController 테스트")
class StudyInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 기본 정보 조회 API [GET /api/studies/{studyId}]")
    class getInformation {
        private static final String BASE_URL = "/api/studies/{studyId}";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("스터디 상세 페이지 기본 정보를 조회한다")
        void success() throws Exception {
            // given
            StudyInformation response = generateStudyInformationResponse();
            given(studyInformationService.getInformation(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Basic",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("스터디 ID(PK)"),
                                            fieldWithPath("name").description("스터디명"),
                                            fieldWithPath("description").description("스터디 설명"),
                                            fieldWithPath("category").description("스터디 카테고리"),
                                            fieldWithPath("thumbnail").description("스터디 썸네일 이미지"),
                                            fieldWithPath("thumbnailBackground").description("스터디 썸네일 배경색"),
                                            fieldWithPath("type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("location.province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .attributes(constraint("온라인 스터디 = null")),
                                            fieldWithPath("location.city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .attributes(constraint("온라인 스터디 = null")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("hashtags[]").description("스터디 해시태그"),
                                            fieldWithPath("minimumAttendanceForGraduation").description("스터디 졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("remainingOpportunityToUpdateGraduationPolicy").description("남은 졸업 요건 변경 횟수"),
                                            fieldWithPath("participants[].id").description("스터디 참여자 ID(PK)"),
                                            fieldWithPath("participants[].nickname").description("스터디 참여자 닉네임"),
                                            fieldWithPath("participants[].gender").description("스터디 참여자 성별"),
                                            fieldWithPath("participants[].score").description("스터디 참여자 점수"),
                                            fieldWithPath("participants[].age").description("스터디 참여자 나이"),
                                            fieldWithPath("host.id").description("스터디 팀장 ID(PK)"),
                                            fieldWithPath("host.nickname").description("스터디 팀장 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 리뷰 조회 API [GET /api/studies/{studyId}/reviews]")
    class getReviews {
        private static final String BASE_URL = "/api/studies/{studyId}/reviews";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("스터디 리뷰 리스트를 조회한다")
        void success() throws Exception {
            // given
            ReviewAssembler response = new ReviewAssembler(
                    9,
                    List.of(
                            new ReviewInformation(new StudyMember(1L, "닉네임1"), "리뷰1", LocalDateTime.now().minusDays(1)),
                            new ReviewInformation(new StudyMember(2L, "닉네임2"), "리뷰2", LocalDateTime.now().minusDays(2)),
                            new ReviewInformation(new StudyMember(3L, "닉네임3"), "리뷰3", LocalDateTime.now().minusDays(3)),
                            new ReviewInformation(new StudyMember(4L, "닉네임4"), "리뷰4", LocalDateTime.now().minusDays(4)),
                            new ReviewInformation(new StudyMember(5L, "닉네임5"), "리뷰5", LocalDateTime.now().minusDays(5))
                    )
            );
            given(studyInformationService.getReviews(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Review",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("graduateCount").description("졸업한 사람 수"),
                                            fieldWithPath("reviews[].reviewer.id").description("리뷰어 ID(PK)"),
                                            fieldWithPath("reviews[].reviewer.nickname").description("리뷰어 닉네임"),
                                            fieldWithPath("reviews[].content").description("리뷰 내용"),
                                            fieldWithPath("reviews[].reviewDate").description("리뷰 작성 날짜")
                                                    .attributes(constraint("날짜 내림차순 정렬로 응답"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 공지사항 조회 API [GET /api/studies/{studyId}/notices] - AccessToken 필수")
    class getNotices {
        private static final String BASE_URL = "/api/studies/{studyId}/notices";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 스터디 공지사항 조회에 실패한다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Information/Notice/Failure",
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
        @DisplayName("스터디 공지사항 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            NoticeAssembler response = generateStudyNotices(5);
            given(studyInformationService.getNotices(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Notice/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("공지사항 ID(PK)"),
                                            fieldWithPath("result[].title").description("공지사항 제목"),
                                            fieldWithPath("result[].content").description("공지사항 내용"),
                                            fieldWithPath("result[].createdAt").description("공지사항 작성 날짜"),
                                            fieldWithPath("result[].modifiedAt").description("공지사항 수정 날짜"),
                                            fieldWithPath("result[].writer.id").description("공지사항 작성자 ID(PK)"),
                                            fieldWithPath("result[].writer.nickname").description("공지사항 작성자 닉네임"),
                                            fieldWithPath("result[].comments[].id").description("공지사항 댓글 ID(PK)"),
                                            fieldWithPath("result[].comments[].noticeId").description("공지사항 ID(PK)"),
                                            fieldWithPath("result[].comments[].content").description("공지사항 댓글 내용"),
                                            fieldWithPath("result[].comments[].writer.id").description("공지사항 댓글 작성자 ID(PK)"),
                                            fieldWithPath("result[].comments[].writer.nickname").description("공지사항 댓글 작성자 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 신청자 조회 API [GET /api/studies/{studyId}/applicants] - AccessToken 필수")
    class getApplicants {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, PARTICIPANT_ID, false);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 신청자 정보를 조회할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(PARTICIPANT_ID);

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
                                    "StudyApi/Information/Applicants/Failure",
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
        @DisplayName("스터디 신청자 정보를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            StudyApplicant response = new StudyApplicant(
                    List.of(
                            new StudyApplicantInformation(1L, "닉네임1", 100, LocalDateTime.now().minusDays(1)),
                            new StudyApplicantInformation(2L, "닉네임2", 92, LocalDateTime.now().minusDays(2)),
                            new StudyApplicantInformation(3L, "닉네임3", 93, LocalDateTime.now().minusDays(3)),
                            new StudyApplicantInformation(4L, "닉네임4", 98, LocalDateTime.now().minusDays(4)),
                            new StudyApplicantInformation(5L, "닉네임5", 95, LocalDateTime.now().minusDays(5))
                    )
            );
            given(studyInformationService.getApplicants(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Applicants/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("applicants[].id").description("신청자 ID(PK)"),
                                            fieldWithPath("applicants[].nickname").description("신청자 닉네임"),
                                            fieldWithPath("applicants[].score").description("신청자 점수"),
                                            fieldWithPath("applicants[].applyDate").description("신청 날짜")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여자 조회 API [GET /api/studies/{studyId}/participants] - AccessToken 필수")
    class getApproveParticipants {
        private static final String BASE_URL = "/api/studies/{studyId}/participants";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;

        @Test
        @DisplayName("스터디 신청자 정보를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            StudyParticipant response = new StudyParticipant(
                    new StudyMember(1L, "팀장"),
                    List.of(
                            new StudyMember(2L, "참여자1"),
                            new StudyMember(3L, "참여자2"),
                            new StudyMember(4L, "참여자3"),
                            new StudyMember(5L, "참여자4")
                    )
            );
            given(studyInformationService.getApproveParticipants(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Participants",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("host.id").description("팀장 ID(PK)"),
                                            fieldWithPath("host.nickname").description("팀장 닉네임"),
                                            fieldWithPath("participants[].id").description("참여자 ID(PK)"),
                                            fieldWithPath("participants[].nickname").description("참여자 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 출석 정보 조회 API [GET /api/studies/{studyId}/attendances] - AccessToken 필수")
    class getAttendances {
        private static final String BASE_URL = "/api/studies/{studyId}/attendances";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 스터디 주차별 출석 정보를 조회할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Information/Attendances/Failure",
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
        @DisplayName("스터디 주차별 출석 정보를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            AttendanceAssmbler response = generateStudyAttendances();
            given(studyInformationService.getAttendances(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Attendances/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("summaries.*").description("스터디 참여자"),
                                            fieldWithPath("summaries.*[].week").description("스터디 주차"),
                                            fieldWithPath("summaries.*[].status").description("해당 주차에 대한 출석 상태")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 정보 조회 API [GET /api/studies/{studyId}/weeks] - AccessToken 필수")
    class getWeeks {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 스터디 주차별 정보를 조회할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
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
                                    "StudyApi/Information/Weeks/Failure",
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
        @DisplayName("스터디 주차별 정보를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(HOST_ID);

            WeeklyAssembler response = generateStudyWeeks();
            given(studyInformationService.getWeeks(STUDY_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Information/Weeks/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("weeks[].id").description("스터디 주차 ID(PK)"),
                                            fieldWithPath("weeks[].title").description("스터디 주차 제목"),
                                            fieldWithPath("weeks[].content").description("스터디 주차 내용"),
                                            fieldWithPath("weeks[].week").description("스터디 주차 주 정보"),
                                            fieldWithPath("weeks[].period.startDate").description("스터디 주차 시작날짜"),
                                            fieldWithPath("weeks[].period.endDate").description("스터디 주차 종료날짜"),
                                            fieldWithPath("weeks[].creator.id").description("스터디 주차 생성자 ID(PK)"),
                                            fieldWithPath("weeks[].creator.nickname").description("스터디 주차 생성자 닉네임"),
                                            fieldWithPath("weeks[].assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            fieldWithPath("weeks[].autoAttendance").description("스터디 주차 자동 출석 여부"),
                                            fieldWithPath("weeks[].attachments[]").description("스터디 주차 첨부파일")
                                                    .optional(),
                                            fieldWithPath("weeks[].attachments[].uploadFileName").description("스터디 주차 첨부파일 업로드 파일명")
                                                    .optional(),
                                            fieldWithPath("weeks[].attachments[].link").description("스터디 주차 첨부파일 S3 업로드명")
                                                    .optional(),
                                            fieldWithPath("weeks[].submits[]").description("스터디 주차 과제 관련 정보")
                                                    .optional(),
                                            fieldWithPath("weeks[].submits[].participant.id").description("스터디 주차 과제 제출자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("weeks[].submits[].participant.nickname").description("스터디 주차 과제 제출자 닉네임")
                                                    .optional(),
                                            fieldWithPath("weeks[].submits[].submitType").description("스터디 주차 과제 제출 타입")
                                                    .optional(),
                                            fieldWithPath("weeks[].submits[].submitFileName").description("스터디 주차 과제 제출 파일명")
                                                    .optional()
                                                    .attributes(constraint("링크 제출 = null / 파일 제출 = 원본 파일명")),
                                            fieldWithPath("weeks[].submits[].submitLink").description("스터디 주차 과제 제출 링크")
                                                    .optional()
                                    )
                            )
                    );
        }
    }

    private StudyInformation generateStudyInformationResponse() {
        Member host = generateHost();
        Member participant = generateParticipant();

        Study study = generateStudy(host);
        study.applyParticipation(participant);
        study.approveParticipation(participant);
        return new StudyInformation(study);
    }

    private Member generateHost() {
        Member member = JIWON.toMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Member generateParticipant() {
        Member member = GHOST.toMember();
        ReflectionTestUtils.setField(member, "id", 2L);
        return member;
    }

    private Study generateStudy(Member host) {
        Study study = TOSS_INTERVIEW.toOfflineStudy(host);
        ReflectionTestUtils.setField(study, "id", 1L);
        return study;
    }

    private NoticeAssembler generateStudyNotices(int count) {
        List<NoticeInformation> list = new ArrayList<>();

        for (long index = 1; index <= count; index++) {
            NoticeInformation noticeInformation = buildNotice(index);
            list.add(noticeInformation);
        }

        return new NoticeAssembler(list);
    }

    private NoticeInformation buildNotice(long id) {
        List<CommentInformation> comments = new ArrayList<>();
        for (long index = 1; index <= 3; index++) {
            comments.add(new CommentInformation(
                    index,
                    id,
                    "댓글",
                    new StudyMember(generateRandomId(), "댓글작성자")
            ));
        }

        return new NoticeInformation(
                id,
                "제목",
                "내용",
                LocalDateTime.now().minusDays(id),
                LocalDateTime.now().minusDays(id),
                new StudyMember(generateRandomId(), "공지사항작성자"),
                comments
        );
    }

    private Long generateRandomId() {
        return (long) (Math.random() * 10) + 1;
    }

    private AttendanceAssmbler generateStudyAttendances() {
        Map<StudyAttendanceMember, List<AttendanceSummary>> summaries = new HashMap<>();
        summaries.put(
                new StudyAttendanceMember(1L, "참여자1", APPROVE),
                List.of(
                        new AttendanceSummary(1, ATTENDANCE.getDescription()),
                        new AttendanceSummary(2, ATTENDANCE.getDescription()),
                        new AttendanceSummary(3, ATTENDANCE.getDescription()),
                        new AttendanceSummary(4, NON_ATTENDANCE.getDescription())
                )
        );
        summaries.put(
                new StudyAttendanceMember(2L, "참여자2", CALCEL),
                List.of(
                        new AttendanceSummary(1, ATTENDANCE.getDescription()),
                        new AttendanceSummary(2, ATTENDANCE.getDescription()),
                        new AttendanceSummary(3, LATE.getDescription())
                )
        );
        summaries.put(
                new StudyAttendanceMember(3L, "참여자3", APPROVE),
                List.of(
                        new AttendanceSummary(1, ATTENDANCE.getDescription()),
                        new AttendanceSummary(2, ABSENCE.getDescription()),
                        new AttendanceSummary(3, LATE.getDescription()),
                        new AttendanceSummary(4, ATTENDANCE.getDescription())
                )
        );
        summaries.put(
                new StudyAttendanceMember(4L, "참여자4", APPROVE),
                List.of(
                        new AttendanceSummary(1, ATTENDANCE.getDescription()),
                        new AttendanceSummary(2, ATTENDANCE.getDescription()),
                        new AttendanceSummary(3, ATTENDANCE.getDescription()),
                        new AttendanceSummary(4, ATTENDANCE.getDescription())
                )
        );
        summaries.put(
                new StudyAttendanceMember(5L, "참여자5", GRADUATED),
                List.of(
                        new AttendanceSummary(1, ATTENDANCE.getDescription()),
                        new AttendanceSummary(2, ATTENDANCE.getDescription()),
                        new AttendanceSummary(3, ATTENDANCE.getDescription())
                )
        );

        return new AttendanceAssmbler(summaries);
    }

    private WeeklyAssembler generateStudyWeeks() {
        List<WeeklySummary> weeks = new ArrayList<>();

        weeks.add(new WeeklySummary(
                6L,
                STUDY_WEEKLY_6.getTitle(),
                STUDY_WEEKLY_6.getContent(),
                STUDY_WEEKLY_6.getWeek(),
                STUDY_WEEKLY_6.getPeriod().toPeriod(),
                new StudyMember(1L, "닉네임1"),
                STUDY_WEEKLY_6.isAssignmentExists(),
                STUDY_WEEKLY_6.isAutoAttendance(),
                STUDY_WEEKLY_6.getAttachments(),
                List.of()
        ));
        weeks.add(new WeeklySummary(
                5L,
                STUDY_WEEKLY_5.getTitle(),
                STUDY_WEEKLY_5.getContent(),
                STUDY_WEEKLY_5.getWeek(),
                STUDY_WEEKLY_5.getPeriod().toPeriod(),
                new StudyMember(1L, "닉네임1"),
                STUDY_WEEKLY_5.isAssignmentExists(),
                STUDY_WEEKLY_5.isAutoAttendance(),
                STUDY_WEEKLY_5.getAttachments(),
                List.of()
        ));
        weeks.add(new WeeklySummary(
                4L,
                STUDY_WEEKLY_4.getTitle(),
                STUDY_WEEKLY_4.getContent(),
                STUDY_WEEKLY_4.getWeek(),
                STUDY_WEEKLY_4.getPeriod().toPeriod(),
                new StudyMember(1L, "닉네임1"),
                STUDY_WEEKLY_4.isAssignmentExists(),
                STUDY_WEEKLY_4.isAutoAttendance(),
                STUDY_WEEKLY_4.getAttachments(),
                List.of(
                        new WeeklySubmitSummary(
                                new StudyMember(1L, "닉네임1"),
                                LINK.name(),
                                null,
                                "https://notion.so"
                        ),
                        new WeeklySubmitSummary(
                                new StudyMember(2L, "닉네임2"),
                                FILE.name(),
                                "hello3.pdf",
                                "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf"
                        )
                )
        ));
        weeks.add(new WeeklySummary(
                3L,
                STUDY_WEEKLY_3.getTitle(),
                STUDY_WEEKLY_3.getContent(),
                STUDY_WEEKLY_3.getWeek(),
                STUDY_WEEKLY_3.getPeriod().toPeriod(),
                new StudyMember(1L, "닉네임1"),
                STUDY_WEEKLY_3.isAssignmentExists(),
                STUDY_WEEKLY_3.isAutoAttendance(),
                STUDY_WEEKLY_3.getAttachments(),
                List.of(
                        new WeeklySubmitSummary(
                                new StudyMember(3L, "닉네임3"),
                                LINK.name(),
                                null,
                                "https://notion.so"
                        )
                )
        ));
        weeks.add(new WeeklySummary(
                2L,
                STUDY_WEEKLY_2.getTitle(),
                STUDY_WEEKLY_2.getContent(),
                STUDY_WEEKLY_2.getWeek(),
                STUDY_WEEKLY_2.getPeriod().toPeriod(),
                new StudyMember(1L, "닉네임1"),
                STUDY_WEEKLY_2.isAssignmentExists(),
                STUDY_WEEKLY_2.isAutoAttendance(),
                STUDY_WEEKLY_2.getAttachments(),
                List.of(
                        new WeeklySubmitSummary(
                                new StudyMember(1L, "닉네임1"),
                                LINK.name(),
                                null,
                                "https://notion.so"
                        ),
                        new WeeklySubmitSummary(
                                new StudyMember(2L, "닉네임2"),
                                FILE.name(),
                                "hello3.pdf",
                                "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf"
                        ),
                        new WeeklySubmitSummary(
                                new StudyMember(3L, "닉네임3"),
                                LINK.name(),
                                null,
                                "https://notion.so"
                        )
                )
        ));
        weeks.add(new WeeklySummary(
                1L,
                STUDY_WEEKLY_1.getTitle(),
                STUDY_WEEKLY_1.getContent(),
                STUDY_WEEKLY_1.getWeek(),
                STUDY_WEEKLY_1.getPeriod().toPeriod(),
                new StudyMember(1L, "닉네임1"),
                STUDY_WEEKLY_1.isAssignmentExists(),
                STUDY_WEEKLY_1.isAutoAttendance(),
                STUDY_WEEKLY_1.getAttachments(),
                List.of(
                        new WeeklySubmitSummary(
                                new StudyMember(1L, "닉네임1"),
                                LINK.name(),
                                null,
                                "https://notion.so"
                        ),
                        new WeeklySubmitSummary(
                                new StudyMember(2L, "닉네임2"),
                                FILE.name(),
                                "hello3.pdf",
                                "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf"
                        ),
                        new WeeklySubmitSummary(
                                new StudyMember(3L, "닉네임3"),
                                LINK.name(),
                                null,
                                "https://notion.so"
                        )
                )
        ));

        return new WeeklyAssembler(weeks);
    }
}
