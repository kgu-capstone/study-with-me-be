package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.service.dto.response.ReviewAssembler;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
import com.kgu.studywithme.study.service.dto.response.StudyMember;
import com.kgu.studywithme.study.service.dto.response.StudyReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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

        for (int index = 1; index <= count; index++) {
            StudyMember reviewer = new StudyMember((long) index, "Nickname" + index);
            list.add(new StudyReview(reviewer, "좋은 스터디입니다", LocalDateTime.now().minusDays(index)));
        }

        return list;
    }
}
