package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.fixture.StudyFixture;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.service.dto.response.StudyAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
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
import static com.kgu.studywithme.study.utils.PagingConstants.SLICE_PER_PAGE;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_DATE;
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
    private static final Pageable page = PageRequest.of(0, SLICE_PER_PAGE);
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

            DefaultStudyResponse response = DefaultStudyResponse.builder()
                    .result(generateCategoryResult(8))
                    .hasNext(true)
                    .build();
            given(studySearchService.findStudyByCategory(category, sort, page, true)).willReturn(response);

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
                                                    .attributes(constraint("online / offline"))
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].study.id").description("스터디 ID(PK)"),
                                            fieldWithPath("result[].study.name").description("스터디명"),
                                            fieldWithPath("result[].study.description").description("스터디 설명"),
                                            fieldWithPath("result[].study.category").description("스터디 카테고리"),
                                            fieldWithPath("result[].study.type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("result[].study.recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("result[].study.currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("result[].study.maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("result[].study.registerDate").description("스터디 생성 날짜"),
                                            fieldWithPath("result[].study.favoriteCount").description("스터디 찜 횟수"),
                                            fieldWithPath("result[].study.reviewCount").description("스터디 리뷰 횟수"),
                                            fieldWithPath("result[].hashtags").description("스터디 해시태그"),
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
                                                    .attributes(constraint("online / offline"))
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
            final Long memberId = 1L;
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(memberId);

            DefaultStudyResponse response = DefaultStudyResponse.builder()
                    .result(generateRecommendResult(8))
                    .hasNext(true)
                    .build();
            given(studySearchService.findStudyByRecommend(memberId, sort, page, true)).willReturn(response);

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
                                                    .attributes(constraint("online / offline"))
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].study.id").description("스터디 ID(PK)"),
                                            fieldWithPath("result[].study.name").description("스터디명"),
                                            fieldWithPath("result[].study.description").description("스터디 설명"),
                                            fieldWithPath("result[].study.category").description("스터디 카테고리"),
                                            fieldWithPath("result[].study.type").description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("result[].study.recruitmentStatus").description("스터디 모집 여부"),
                                            fieldWithPath("result[].study.currentMembers").description("스터디 참여 인원"),
                                            fieldWithPath("result[].study.maxMembers").description("스터디 최대 인원"),
                                            fieldWithPath("result[].study.registerDate").description("스터디 생성 날짜"),
                                            fieldWithPath("result[].study.favoriteCount").description("스터디 찜 횟수"),
                                            fieldWithPath("result[].study.reviewCount").description("스터디 리뷰 횟수"),
                                            fieldWithPath("result[].hashtags").description("스터디 해시태그"),
                                            fieldWithPath("hasNext").description("다음 스크롤 존재 여부")
                                                    .attributes(constraint("false면 무한 스크롤 종료"))
                                    )

                            )
                    );
        }
    }

    private List<StudyAssembler> generateCategoryResult(int count) {
        List<StudyAssembler> result = new ArrayList<>();
        List<StudyFixture> studyFixtures = Arrays.stream(StudyFixture.values())
                .filter(study -> study.getCategory().equals(PROGRAMMING))
                .toList();

        for (int index = 0; index < count; index++) {
            StudyAssembler build = StudyAssembler.builder()
                    .study(buildStudy(studyFixtures.get(index), index + 1))
                    .hashtags(new ArrayList<>(studyFixtures.get(index).getHashtags()))
                    .build();
            result.add(build);
        }

        return result;
    }

    private List<StudyAssembler> generateRecommendResult(int count) {
        List<StudyAssembler> result = new ArrayList<>();
        List<StudyFixture> studyFixtures = Arrays.stream(StudyFixture.values())
                .filter(study -> study.getCategory().equals(LANGUAGE) || study.getCategory().equals(INTERVIEW) || study.getCategory().equals(PROGRAMMING))
                .toList();

        for (int index = 0; index < count; index++) {
            StudyAssembler build = StudyAssembler.builder()
                    .study(buildStudy(studyFixtures.get(index), index + 1))
                    .hashtags(new ArrayList<>(studyFixtures.get(index).getHashtags()))
                    .build();
            result.add(build);
        }

        return result;
    }

    private static BasicStudy buildStudy(StudyFixture study, long index) {
        return BasicStudy.builder()
                .id(index)
                .name(StudyName.from(study.getName()))
                .description(Description.from(study.getDescription()))
                .category(study.getCategory())
                .type(study.getType())
                .recruitmentStatus(IN_PROGRESS)
                .currentMembers(getRandomNumber())
                .capacity(Capacity.from(study.getCapacity()))
                .registerDate(LocalDateTime.now().minusDays(index))
                .favoriteCount(getRandomNumber())
                .reviewCount(getRandomNumber())
                .build();
    }

    private static int getRandomNumber() {
        return (int) (Math.random() * 7);
    }
}
