package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infra.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.service.dto.response.*;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleGraduatedStudy;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import com.kgu.studywithme.study.infra.query.dto.response.StudyReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
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

@DisplayName("Member [Controller Layer] -> MemberInformationApiController 테스트")
class MemberInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 기본 정보 조회 API [GET /api/members/{memberId}] - AccessToken 필수")
    class getInformation {
        private static final String BASE_URL = "/api/members/{memberId}";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자 기본 정보를 조회한다")
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
                                    "MemberApi/Information/Basic",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
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
                                            fieldWithPath("score").description("사용자 점수"),
                                            fieldWithPath("isEmailOptIn").description("이메일 수신 동의 여부"),
                                            fieldWithPath("interests[]").description("사용자 관심사 목록")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 신청한 스터디 리스트 조회 API [GET /api/members/{memberId}/studies/apply] - AccessToken 필수")
    class getApplyStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies/apply";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("Private한 사용자 정보는 타인이 조회할 수 없다 (Token PayloadId != PathVariable memberId)")
        void throwExceptionByInvalidPermission() throws Exception {
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
                                    "MemberApi/Information/RelatedStudy/Apply/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자가 신청한 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            RelatedStudy response = generateRelatedStudyResponse();
            given(memberInformationService.getApplyStudy(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/RelatedStudy/Apply/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("신청한 스터디 ID(PK)"),
                                            fieldWithPath("result[].name").description("신청한 스터디명"),
                                            fieldWithPath("result[].category").description("신청한 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail").description("신청한 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground").description("신청한 스터디 썸네일 배경색")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 참여중인 스터디 리스트 조회 API [GET /api/members/{memberId}/studies/participate] - AccessToken 필수")
    class getParticipateStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies/participate";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("Private한 사용자 정보는 타인이 조회할 수 없다 (Token PayloadId != PathVariable memberId)")
        void throwExceptionByInvalidPermission() throws Exception {
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
                                    "MemberApi/Information/RelatedStudy/Participate/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자가 참여중인 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            RelatedStudy response = generateRelatedStudyResponse();
            given(memberInformationService.getParticipateStudy(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/RelatedStudy/Participate/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("참여중인 스터디 ID(PK)"),
                                            fieldWithPath("result[].name").description("참여중인 스터디명"),
                                            fieldWithPath("result[].category").description("참여중인 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail").description("참여중인 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground").description("참여중인 스터디 썸네일 배경색")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 찜한 스터디 리스트 조회 API [GET /api/members/{memberId}/studies/favorite] - AccessToken 필수")
    class getFavoriteStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies/favorite";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("Private한 사용자 정보는 타인이 조회할 수 없다 (Token PayloadId != PathVariable memberId)")
        void throwExceptionByInvalidPermission() throws Exception {
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
                                    "MemberApi/Information/RelatedStudy/Favorite/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자가 찜한 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            RelatedStudy response = generateRelatedStudyResponse();
            given(memberInformationService.getFavoriteStudy(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/RelatedStudy/Favorite/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("찜한 스터디 ID(PK)"),
                                            fieldWithPath("result[].name").description("찜한 스터디명"),
                                            fieldWithPath("result[].category").description("찜한 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail").description("찜한 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground").description("찜한 스터디 썸네일 배경색")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 졸업한 스터디 리스트 조회 API [GET /api/members/{memberId}/studies/graduated] - AccessToken 필수")
    class getGraduatedStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies/graduated";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자가 졸업한 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            GraduatedStudy response = generateGraduatedStudyResponse();
            given(memberInformationService.getGraduatedStudy(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/RelatedStudy/Graduated",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("졸업한 스터디 ID(PK)"),
                                            fieldWithPath("result[].name").description("졸업한 스터디명"),
                                            fieldWithPath("result[].category").description("졸업한 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail").description("졸업한 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground").description("졸업한 스터디 썸네일 배경색"),
                                            fieldWithPath("result[].review").description("작성한 리뷰")
                                                    .optional(),
                                            fieldWithPath("result[].review.id").description("작성한 리뷰 ID")
                                                    .optional(),
                                            fieldWithPath("result[].review.content").description("작성한 리뷰")
                                                    .optional(),
                                            fieldWithPath("result[].review.writtenDate").description("리뷰 작성 날짜")
                                                    .optional(),
                                            fieldWithPath("result[].review.lastModifiedDate").description("리뷰 수정 날짜")
                                                    .optional()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 피어리뷰 조회 API [GET /api/members/{memberId}/reviews] - AccessToken 필수")
    class getReviews {
        private static final String BASE_URL = "/api/members/{memberId}/reviews";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자에 대한 피어리뷰 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);

            PeerReviewAssembler response = generatePeerReviewAssembler();
            given(memberInformationService.getPeerReviews(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/PeerReview",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("reviews[]").description("피어리뷰")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 출석률 조회 API [GET /api/members/{memberId}/attendances] - AccessToken 필수")
    class getAttendanceRatio {
        private static final String BASE_URL = "/api/members/{memberId}/attendances";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자의 출석률 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);

            AttendanceRatioAssembler response = generateAttendanceRatio();
            given(memberInformationService.getAttendanceRatio(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/Attendances",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].status").description("출석 상태"),
                                            fieldWithPath("result[].count").description("출석 횟수")
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
        List<SimpleStudy> result = List.of(
                new SimpleStudy(1L, StudyName.from(SPRING.getName()), PROGRAMMING, SPRING.getThumbnail()),
                new SimpleStudy(2L, StudyName.from(JPA.getName()), PROGRAMMING, JPA.getThumbnail()),
                new SimpleStudy(3L, StudyName.from(TOSS_INTERVIEW.getName()), INTERVIEW, TOSS_INTERVIEW.getThumbnail()),
                new SimpleStudy(4L, StudyName.from(AWS.getName()), PROGRAMMING, AWS.getThumbnail())
        );
        return new RelatedStudy(result);
    }

    private GraduatedStudy generateGraduatedStudyResponse() {
        List<SimpleGraduatedStudy> result = List.of(
                new SimpleGraduatedStudy(
                        1L,
                        SPRING.getName(),
                        PROGRAMMING.getName(),
                        SPRING.getThumbnail().getImageName(),
                        SPRING.getThumbnail().getBackground(),
                        null
                ),
                new SimpleGraduatedStudy(
                        2L,
                        JPA.getName(),
                        PROGRAMMING.getName(),
                        JPA.getThumbnail().getImageName(),
                        JPA.getThumbnail().getBackground(),
                        new StudyReview(1L, "hello", LocalDateTime.now(), LocalDateTime.now())
                ),
                new SimpleGraduatedStudy(
                        3L,
                        TOSS_INTERVIEW.getName(),
                        INTERVIEW.getName(),
                        TOSS_INTERVIEW.getThumbnail().getImageName(),
                        TOSS_INTERVIEW.getThumbnail().getBackground(),
                        new StudyReview(2L, "hello", LocalDateTime.now().minusHours(3), LocalDateTime.now())
                ),
                new SimpleGraduatedStudy(
                        4L,
                        AWS.getName(),
                        PROGRAMMING.getName(),
                        AWS.getThumbnail().getImageName(),
                        AWS.getThumbnail().getBackground(),
                        null
                )
        );
        return new GraduatedStudy(result);
    }

    private PeerReviewAssembler generatePeerReviewAssembler() {
        List<String> result = List.of(
                "PeerReview1",
                "PeerReview2",
                "PeerReview3",
                "PeerReview4"
        );
        return new PeerReviewAssembler(result);
    }

    private AttendanceRatioAssembler generateAttendanceRatio() {
        List<AttendanceRatio> result = List.of(
                new AttendanceRatio(ATTENDANCE, 14),
                new AttendanceRatio(LATE, 5),
                new AttendanceRatio(ABSENCE, 1),
                new AttendanceRatio(NON_ATTENDANCE, 0)
        );
        return new AttendanceRatioAssembler(result);
    }
}
