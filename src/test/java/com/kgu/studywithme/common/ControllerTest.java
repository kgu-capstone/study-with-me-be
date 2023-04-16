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
import com.kgu.studywithme.member.controller.MemberApiController;
import com.kgu.studywithme.member.controller.MemberInformationApiController;
import com.kgu.studywithme.member.controller.MemberReviewApiController;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.member.service.MemberInformationService;
import com.kgu.studywithme.member.service.MemberReviewService;
import com.kgu.studywithme.member.service.MemberService;
import com.kgu.studywithme.study.controller.*;
import com.kgu.studywithme.study.controller.attendance.AttendanceApiController;
import com.kgu.studywithme.study.controller.notice.StudyNoticeApiController;
import com.kgu.studywithme.study.controller.notice.StudyNoticeCommentApiController;
import com.kgu.studywithme.study.service.*;
import com.kgu.studywithme.study.service.attendance.AttendanceService;
import com.kgu.studywithme.study.service.notice.NoticeCommentService;
import com.kgu.studywithme.study.service.notice.NoticeService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
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
        AttendanceApiController.class
})
@ExtendWith(RestDocumentationExtension.class)
@Import(TestAopConfiguration.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

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
    protected MemberFindService memberFindService;

    @MockBean
    protected MemberInformationService memberInformationService;

    @MockBean
    protected MemberReviewService memberReviewService;

    // study
    @MockBean
    protected StudyService studyService;

    @MockBean
    protected StudyFindService studyFindService;

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

    protected Attributes.Attribute constraint(String value) {
        return new Attributes.Attribute("constraints", value);
    }

    protected String convertObjectToJson(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}
