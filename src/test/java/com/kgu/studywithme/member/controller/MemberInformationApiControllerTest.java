package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
import com.kgu.studywithme.member.service.dto.response.RelatedStudy;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
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

@DisplayName("Member [Controller Layer] -> MemberInformationApiController 테스트")
class MemberInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 기본 정보 조회 API [GET /api/members/{memberId}]")
    class getInformation {
        private static final String BASE_URL = "/api/members/{memberId}";
        private static final Long MEMBER_ID = 1L;
        
        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 본인 정보 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID);
            
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
                                    "MemberApi/Information/Basic/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
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
        @DisplayName("Token Payload가 Endpoint의 memberId와 일치하지 않음에 따라 사용자 정보 조회에 실패한다")
        void failureByAnonymousMember() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID + 10000L);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));
            
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
                                    "MemberApi/Information/Basic/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
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
        @DisplayName("사용자 상세 페이지 기본 정보를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            MemberInformation response = generateMemberInformationResponse();
            given(memberInformationService.getInformation(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/Basic/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID(PK)"),
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("nickname").description("사용자 닉네임"),
                                            fieldWithPath("email").description("사용자 이메일"),
                                            fieldWithPath("birth").description("사용자 생년월일"),
                                            fieldWithPath("phone").description("사용자 전화번호"),
                                            fieldWithPath("gender").description("사용자 성별"),
                                            fieldWithPath("region.province").description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("region.city").description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("interests[]").description("사용자 관심사 목록")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자와 관련된(참여 & 졸업 & 찜) 스터디 리스트 조회 API [GET /api/members/{memberId}/studies]")
    class getRelatedStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 관련된 스터디 리스트 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID);

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
                                    "MemberApi/Information/RelatedStudy/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
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
        @DisplayName("Token Payload가 Endpoint의 memberId와 일치하지 않음에 따라 관련된 스터디 리스트 조회에 실패한다")
        void failureByAnonymousMember() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID + 10000L);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

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
                                    "MemberApi/Information/RelatedStudy/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
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
        @DisplayName("사용자와 관련된(참여 & 졸업 & 찜) 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            RelatedStudy response = generateRelatedStudyResponse();
            given(memberInformationService.getRelatedStudy(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/RelatedStudy/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("participateStudyList[].id").description("참여중인 스터디 ID(PK)"),
                                            fieldWithPath("participateStudyList[].name").description("참여중인 스터디명"),
                                            fieldWithPath("participateStudyList[].category").description("참여중인 스터디 카테고리"),
                                            fieldWithPath("graduatedStudyList[].id").description("졸업한 스터디 ID(PK)"),
                                            fieldWithPath("graduatedStudyList[].name").description("졸업한 스터디명"),
                                            fieldWithPath("graduatedStudyList[].category").description("졸업한 스터디 카테고리"),
                                            fieldWithPath("favoriteStudyList[].id").description("찜한 스터디 ID(PK)"),
                                            fieldWithPath("favoriteStudyList[].name").description("찜한 스터디명"),
                                            fieldWithPath("favoriteStudyList[].category").description("찜한 스터디 카테고리")
                                    )
                            )
                    );
        }
    }

    private MemberInformation generateMemberInformationResponse() {
        Member member = JIWON.toMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        return new MemberInformation(member);
    }

    private RelatedStudy generateRelatedStudyResponse() {
        List<SimpleStudy> participateStudy = List.of(
                new SimpleStudy(1L, StudyName.from("Spring 스터디"), PROGRAMMING),
                new SimpleStudy(2L, StudyName.from("JPA 스터디"), PROGRAMMING),
                new SimpleStudy(3L, StudyName.from("Toss 면접 스터디"), INTERVIEW),
                new SimpleStudy(4L, StudyName.from("AWS 스터디"), PROGRAMMING)
        );
        List<SimpleStudy> graduatedStudy = List.of(
                new SimpleStudy(5L, StudyName.from("Naver 면접 스터디"), INTERVIEW),
                new SimpleStudy(6L, StudyName.from("일본어 스터디"), LANGUAGE)
        );
        List<SimpleStudy> favoriteStudy = List.of(
                new SimpleStudy(7L, StudyName.from("아랍어 스터디"), LANGUAGE),
                new SimpleStudy(8L, StudyName.from("코틀린 스터디"), PROGRAMMING),
                new SimpleStudy(9L, StudyName.from("Real MySQL 스터디"), PROGRAMMING),
                new SimpleStudy(10L, StudyName.from("파이썬 스터디"), PROGRAMMING),
                new SimpleStudy(11L, StudyName.from("Kubernetes 스터디"), PROGRAMMING),
                new SimpleStudy(12L, StudyName.from("운영체제 스터디"), PROGRAMMING),
                new SimpleStudy(13L, StudyName.from("Kakao 면접 스터디"), INTERVIEW)
        );

        return new RelatedStudy(participateStudy, graduatedStudy, favoriteStudy);
    }
}
