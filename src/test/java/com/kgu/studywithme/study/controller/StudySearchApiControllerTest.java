package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.fixture.StudyFixture;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_DATE;
import static com.kgu.studywithme.study.utils.PagingConstants.getDefaultPageRequest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> StudySearchApiController 테스트")
class StudySearchApiControllerTest extends ControllerTest {
    private static final String sort = SORT_DATE;
    private static final Pageable page = getDefaultPageRequest(0);
    private static final String type = "online";

    @Nested
    @DisplayName("각 카테고리별 스터디 조회 API [GET /api/studies]")
    class findStudyByCategory {
        private static final String BASE_URL = "/api/studies";

        @Test
        @DisplayName("프로그래밍 카테고리로 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            final Category category = PROGRAMMING;
            StudyCategoryCondition condition = new StudyCategoryCondition(category, sort, type, null, null);

            DefaultStudyResponse response = new DefaultStudyResponse(generateCategoryResult(8), true);
            given(studySearchService.findStudyByCategory(condition, page)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("category", String.valueOf(category.getId()))
                    .param("sort", sort)
                    .param("page", String.valueOf(0))
                    .param("type", type);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Search/Category",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("category").description("선택한 카테고리의 ID"),
                                            parameterWithName("sort").description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type").description("온라인/오프라인 유무")
                                                    .attributes(constraint("null(온 + 오프) / online / offline")),
                                            parameterWithName("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화")),
                                            parameterWithName("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화"))
                                    ),
                                    responseFields(
                                            fieldWithPath("studyList[].id").description("스터디 ID(PK)"),
                                            fieldWithPath("studyList[].name").description("스터디명"),
                                            fieldWithPath("studyList[].description").description("스터디 설명"),
                                            fieldWithPath("studyList[].category").description("스터디 카테고리"),
                                            fieldWithPath("studyList[].type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("studyList[].recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("studyList[].currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("studyList[].maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("studyList[].registerDate").description("스터디 생성 날짜"),
                                            fieldWithPath("studyList[].favoriteCount").description("스터디 찜 횟수"),
                                            fieldWithPath("studyList[].reviewCount").description("스터디 리뷰 횟수"),
                                            fieldWithPath("studyList[].hashtags[]").description("스터디 해시태그"),
                                            fieldWithPath("hasNext").description("다음 스크롤 존재 여부")
                                                    .attributes(constraint("false면 무한 스크롤 종료"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 관심사에 따른 스터디 조회 API [GET /api/studies/recommend]")
    class findStudyByRecommend {
        private static final String BASE_URL = "/api/studies/recommend";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 추천에 따른 스터디 리스트 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(0))
                    .param("type", type);

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
                                    "StudyApi/Search/Recommend/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type").description("온라인/오프라인 유무")
                                                    .attributes(constraint("null(온 + 오프) / online / offline")),
                                            parameterWithName("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화")),
                                            parameterWithName("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화"))
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
        @DisplayName("사용자의 관심사에 따른 스터디 리스트를 조회한다 [언어 / 면접 / 프로그래밍]")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            StudyRecommendCondition condition = new StudyRecommendCondition(MEMBER_ID, sort, type, null, null);

            DefaultStudyResponse response = new DefaultStudyResponse(generateRecommendResult(8), true);
            given(studySearchService.findStudyByRecommend(condition, page)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("sort", sort)
                    .param("page", String.valueOf(0))
                    .param("type", type);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Search/Recommend/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type").description("온라인/오프라인 유무")
                                                    .attributes(constraint("null(온 + 오프) / online / offline")),
                                            parameterWithName("province").description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화")),
                                            parameterWithName("city").description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화"))
                                    ),
                                    responseFields(
                                            fieldWithPath("studyList[].id").description("스터디 ID(PK)"),
                                            fieldWithPath("studyList[].name").description("스터디명"),
                                            fieldWithPath("studyList[].description").description("스터디 설명"),
                                            fieldWithPath("studyList[].category").description("스터디 카테고리"),
                                            fieldWithPath("studyList[].type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("studyList[].recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("studyList[].currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("studyList[].maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("studyList[].registerDate").description("스터디 생성 날짜"),
                                            fieldWithPath("studyList[].favoriteCount").description("스터디 찜 횟수"),
                                            fieldWithPath("studyList[].reviewCount").description("스터디 리뷰 횟수"),
                                            fieldWithPath("studyList[].hashtags[]").description("스터디 해시태그"),
                                            fieldWithPath("hasNext").description("다음 스크롤 존재 여부")
                                                    .attributes(constraint("false면 무한 스크롤 종료"))
                                    )
                            )
                    );
        }
    }

    private List<BasicStudy> generateCategoryResult(int count) {
        List<BasicStudy> result = new ArrayList<>();
        List<StudyFixture> studyFixtures = Arrays.stream(StudyFixture.values())
                .filter(study -> study.getCategory().equals(PROGRAMMING))
                .toList();

        for (int index = 0; index < count; index++) {
            result.add(buildStudy(studyFixtures.get(index), index + 1));
        }

        return result;
    }

    private List<BasicStudy> generateRecommendResult(int count) {
        List<BasicStudy> result = new ArrayList<>();
        List<StudyFixture> studyFixtures = Arrays.stream(StudyFixture.values())
                .filter(study -> study.getCategory().equals(LANGUAGE) || study.getCategory().equals(INTERVIEW) || study.getCategory().equals(PROGRAMMING))
                .toList();

        for (int index = 0; index < count; index++) {
            result.add(buildStudy(studyFixtures.get(index), index + 1));
        }

        return result;
    }

    private static BasicStudy buildStudy(StudyFixture study, long index) {
        return BasicStudy.builder()
                .id(index)
                .name(study.getName())
                .description(study.getDescription())
                .category(study.getCategory().getName())
                .type(study.getType().getDescription())
                .recruitmentStatus(IN_PROGRESS.getDescription())
                .currentMembers(getRandomNumber())
                .maxMembers(study.getCapacity())
                .registerDate(LocalDateTime.now().minusDays(index))
                .favoriteCount(getRandomNumber())
                .reviewCount(getRandomNumber())
                .hashtags(new ArrayList<>(study.getHashtags()))
                .build();
    }

    private static int getRandomNumber() {
        return (int) (Math.random() * 7);
    }
}
