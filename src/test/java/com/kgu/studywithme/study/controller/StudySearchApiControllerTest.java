package com.kgu.studywithme.study.controller;

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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> StudySearchApiController 테스트")
class StudySearchApiControllerTest extends ControllerTest {
    private static final String SORT = SORT_DATE;
    private static final Pageable PAGE = getDefaultPageRequest(0);
    private static final String TYPE = "online";

    @Nested
    @DisplayName("각 카테고리별 스터디 조회 API [GET /api/studies]")
    class findStudyByCategory {
        private static final String BASE_URL = "/api/studies";

        @Test
        @DisplayName("프로그래밍 카테고리로 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            final Category category = PROGRAMMING;
            StudyCategoryCondition condition = new StudyCategoryCondition(category, SORT, TYPE, null, null);

            DefaultStudyResponse response = new DefaultStudyResponse(generateCategoryResult(8), true);
            given(studySearchService.findStudyByCategory(condition, PAGE)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("category", String.valueOf(category.getId()))
                    .param("sort", SORT)
                    .param("page", String.valueOf(0))
                    .param("type", TYPE);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Search/Category",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("category").description("카테고리 ID"),
                                            parameterWithName("sort").description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type").description("온라인/오프라인 유무")
                                                    .optional()
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
                                            fieldWithPath("studyList[].thumbnail").description("스터디 썸네일 이미지"),
                                            fieldWithPath("studyList[].thumbnailBackground").description("스터디 썸네일 배경색"),
                                            fieldWithPath("studyList[].type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("studyList[].recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("studyList[].currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("studyList[].maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("studyList[].registerDate").description("스터디 생성 날짜"),
                                            fieldWithPath("studyList[].hashtags[]").description("스터디 해시태그"),
                                            fieldWithPath("studyList[].favoriteMarkingMembers[]").description("스터디 찜 사용자 ID(PK) 리스트"),
                                            fieldWithPath("hasNext").description("다음 스크롤 존재 여부")
                                                    .attributes(constraint("false면 무한 스크롤 종료"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 관심사에 따른 스터디 조회 API [GET /api/studies/recommend] - AccessToken 필수")
    class findStudyByRecommend {
        private static final String BASE_URL = "/api/studies/recommend";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자의 관심사에 따른 스터디 리스트를 조회한다 [언어 / 면접 / 프로그래밍]")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            StudyRecommendCondition condition = new StudyRecommendCondition(MEMBER_ID, SORT, TYPE, null, null);

            DefaultStudyResponse response = new DefaultStudyResponse(generateRecommendResult(8), true);
            given(studySearchService.findStudyByRecommend(condition, PAGE)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .param("sort", SORT)
                    .param("page", String.valueOf(0))
                    .param("type", TYPE);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Search/Recommend",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type").description("온라인/오프라인 유무")
                                                    .optional()
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
                                            fieldWithPath("studyList[].thumbnail").description("스터디 썸네일"),
                                            fieldWithPath("studyList[].thumbnailBackground").description("스터디 썸네일 배경색"),
                                            fieldWithPath("studyList[].type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("studyList[].recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("studyList[].currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("studyList[].maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("studyList[].registerDate").description("스터디 생성 날짜"),
                                            fieldWithPath("studyList[].hashtags[]").description("스터디 해시태그"),
                                            fieldWithPath("studyList[].favoriteMarkingMembers[]").description("스터디 찜 사용자 ID(PK) 리스트"),
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

    private BasicStudy buildStudy(StudyFixture study, long index) {
        return BasicStudy.builder()
                .id(index)
                .name(study.getName())
                .description(study.getDescription())
                .category(study.getCategory().getName())
                .thumbnail(study.getThumbnail().getImageName())
                .thumbnailBackground(study.getThumbnail().getBackground())
                .type(study.getType().getDescription())
                .recruitmentStatus(IN_PROGRESS.getDescription())
                .currentMembers(getRandomNumberWithRange7())
                .maxMembers(study.getCapacity())
                .registerDate(LocalDateTime.now().minusDays(index))
                .hashtags(new ArrayList<>(study.getHashtags()))
                .favoriteMarkingMembers(generateRandomList(getRandomNumberWithRange7()))
                .build();
    }

    private int getRandomNumberWithRange7() {
        return (int) (Math.random() * 7);
    }

    private List<Long> generateRandomList(int count) {
        List<Long> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            result.add(getRandomNumberWithRange1000());
        }

        return result;
    }

    private Long getRandomNumberWithRange1000() {
        return (long) (Math.random() * 100 + 1);
    }
}
