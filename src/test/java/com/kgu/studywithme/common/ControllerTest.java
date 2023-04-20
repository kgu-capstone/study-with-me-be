package com.kgu.studywithme.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.controller.OAuthApiController;
import com.kgu.studywithme.auth.controller.TokenReissueApiController;
import com.kgu.studywithme.auth.infra.oauth.OAuthUri;
import com.kgu.studywithme.auth.service.OAuthService;
import com.kgu.studywithme.auth.service.TokenReissueService;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.category.controller.CategoryApiController;
import com.kgu.studywithme.category.service.CategoryService;
import com.kgu.studywithme.common.config.TestAopConfiguration;
import com.kgu.studywithme.favorite.controller.FavoriteApiController;
import com.kgu.studywithme.favorite.service.FavoriteManageService;
import com.kgu.studywithme.fixture.MemberFixture;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.controller.MemberApiController;
import com.kgu.studywithme.member.controller.MemberInformationApiController;
import com.kgu.studywithme.member.controller.MemberReviewApiController;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.member.service.MemberInformationService;
import com.kgu.studywithme.member.service.MemberReviewService;
import com.kgu.studywithme.member.service.MemberService;
import com.kgu.studywithme.study.controller.*;
import com.kgu.studywithme.study.controller.attendance.AttendanceApiController;
import com.kgu.studywithme.study.controller.notice.StudyNoticeApiController;
import com.kgu.studywithme.study.controller.notice.StudyNoticeCommentApiController;
import com.kgu.studywithme.study.controller.week.StudyWeeklyApiController;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.*;
import com.kgu.studywithme.study.service.attendance.AttendanceService;
import com.kgu.studywithme.study.service.notice.NoticeCommentService;
import com.kgu.studywithme.study.service.notice.NoticeService;
import com.kgu.studywithme.study.service.week.StudyWeeklyService;
import com.kgu.studywithme.upload.controller.UploadApiController;
import com.kgu.studywithme.upload.utils.FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest({
        // auth
        OAuthApiController.class, TokenReissueApiController.class,

        // category & favorite
        CategoryApiController.class, FavoriteApiController.class,

        // member
        MemberApiController.class, MemberInformationApiController.class, MemberReviewApiController.class,

        // study
        StudyApiController.class, StudyInformationApiController.class, StudyParticipationApiController.class,
        StudyReviewApiController.class, StudySearchApiController.class,
        StudyNoticeApiController.class, StudyNoticeCommentApiController.class,
        AttendanceApiController.class, StudyWeeklyApiController.class,

        // upload
        UploadApiController.class,
})
@ExtendWith(RestDocumentationExtension.class)
@Import(TestAopConfiguration.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common & external
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    // common & internal
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberFindService memberFindService;

    @MockBean
    private StudyFindService studyFindService;

    @MockBean
    private StudyValidator studyValidator;

    // auth
    @MockBean
    protected OAuthUri oAuthUri;

    @MockBean
    protected OAuthService oAuthService;

    @MockBean
    protected TokenReissueService tokenReissueService;

    // category & favorite
    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected FavoriteManageService favoriteManageService;

    // member
    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MemberInformationService memberInformationService;

    @MockBean
    protected MemberReviewService memberReviewService;

    // study
    @MockBean
    protected StudyService studyService;

    @MockBean
    protected StudyInformationService studyInformationService;

    @MockBean
    protected ParticipationService participationService;

    @MockBean
    protected StudyReviewService studyReviewService;

    @MockBean
    protected StudySearchService studySearchService;

    @MockBean
    protected NoticeService noticeService;

    @MockBean
    protected NoticeCommentService commentService;

    @MockBean
    protected AttendanceService attendanceService;

    @MockBean
    protected StudyWeeklyService studyWeeklyService;

    // upload
    @MockBean
    protected FileUploader uploader;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(prettyPrint());
    }

    protected OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    protected Snippet getHeaderWithAccessToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Access Token")
        );
    }

    protected Snippet getHeaderWithRefreshToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Refresh Token")
        );
    }

    protected Snippet getExceptionResponseFiels() {
        return responseFields(
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("errorCode").description("커스텀 예외 코드"),
                fieldWithPath("message").description("예외 메시지")
        );
    }

    protected Attributes.Attribute constraint(String value) {
        return new Attributes.Attribute("constraints", value);
    }

    protected String convertObjectToJson(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    protected void mockingForStudyParticipant(Study study, MemberFixture fixture, Long memberId, boolean isValid) {
        Member member = createMember(fixture, memberId);

        if (isValid) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    protected void mockingForStudyHost(Long studyId, Long memberId, boolean isValid) {
        if (isValid) {
            doNothing()
                    .when(studyValidator)
                    .validateHost(studyId, memberId);
        } else {
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                    .when(studyValidator)
                    .validateHost(studyId, memberId);
        }
    }

    protected Study createSpringStudy(Long hostId, Long studyId) {
        Member host = createMember(JIWON, hostId);
        Study study = SPRING.toOnlineStudy(host);
        ReflectionTestUtils.setField(study, "id", studyId);

        given(studyFindService.findById(studyId)).willReturn(study);
        return study;
    }

    private Member createMember(MemberFixture fixture, Long memberId) {
        Member member = fixture.toMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(memberFindService.findById(memberId)).willReturn(member);
        return member;
    }
}
