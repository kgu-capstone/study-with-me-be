package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.infra.query.dto.response.CommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
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
import java.util.List;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
                                            fieldWithPath("type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("area.province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .attributes(constraint("온라인 스터디 = null")),
                                            fieldWithPath("area.city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .attributes(constraint("온라인 스터디 = null")),
                                            fieldWithPath("recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("averageAge").description("스터디 참여자 평균 나이"),
                                            fieldWithPath("hashtags[]").description("스터디 해시태그"),
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
            ReviewAssembler response = generateStudyReviewAssembler();
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
    @DisplayName("스터디 공지사항 조회 API [GET /api/studies/{studyId}/notices]")
    class getNotices {
        private static final String BASE_URL = "/api/studies/{studyId}/notices";
        private static final Long STUDY_ID = 1L;

        @BeforeEach
        void setUp() {
            Member host = JIWON.toMember();
            Member memberA = DUMMY1.toMember();
            Member memberB = DUMMY2.toMember();
            setIdByReflection(host, 1L);
            setIdByReflection(memberA, 2L);
            setIdByReflection(memberB, 3L);
            given(memberFindService.findById(1L)).willReturn(host);
            given(memberFindService.findById(2L)).willReturn(memberA);
            given(memberFindService.findById(3L)).willReturn(memberB);

            Study study = SPRING.toOnlineStudy(host);
            setIdByReflection(memberA, 1L);
            given(studyFindService.findByIdWithHost(1L)).willReturn(study);

            study.applyParticipation(memberA);
            study.approveParticipation(memberA);
        }

        private void setIdByReflection(Object object, Long id) {
            ReflectionTestUtils.setField(object, "id", id);
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 스터디 공지사항 조회에 실패한다")
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
                                    "StudyApi/Information/Notice/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
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
        @DisplayName("해당 스터디 참여자가 아니면 스터디 공지사항 조회에 실패한다")
        void failureByAnonymousMember() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(3L);

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
                                    "StudyApi/Information/Notice/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
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
        @DisplayName("스터디 공지사항 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(2L);

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
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
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

    private StudyInformation generateStudyInformationResponse() {
        Member host = generateHost();
        Study study = generateStudy(host);
        return new StudyInformation(study);
    }

    private Member generateHost() {
        Member member = JIWON.toMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Study generateStudy(Member host) {
        Study study = TOSS_INTERVIEW.toOfflineStudy(host);
        ReflectionTestUtils.setField(study, "id", 1L);
        return study;
    }

    private ReviewAssembler generateStudyReviewAssembler() {
        int graduateCount = 10;
        List<StudyReview> studyReviews = generateStudyReviews(6);

        return new ReviewAssembler(graduateCount, studyReviews);
    }

    private List<StudyReview> generateStudyReviews(int count) {
        List<StudyReview> list = new ArrayList<>();

        for (long index = 1; index <= count; index++) {
            StudyMember reviewer = new StudyMember(index, "Nickname" + index);
            list.add(new StudyReview(reviewer, "좋은 스터디입니다", LocalDateTime.now().minusDays(index)));
        }

        return list;
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
        NoticeInformation noticeInformation = new NoticeInformation(
                id, "제목", "내용",
                LocalDateTime.now().minusDays(id), LocalDateTime.now().minusDays(id),
                generateRandomId(), Nickname.from("닉네임")
        );

        List<CommentInformation> comments = new ArrayList<>();
        for (long index = 1; index <= 3; index++) {
            CommentInformation information = new CommentInformation(
                    index, id, "댓글 내용", generateRandomId(), Nickname.from("닉네임"));
            comments.add(information);
        }
        noticeInformation.setComments(comments);

        return noticeInformation;
    }

    private Long generateRandomId() {
        return (long) (Math.random() * 10) + 1;
    }
}
