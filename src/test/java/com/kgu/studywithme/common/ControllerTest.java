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
import com.kgu.studywithme.member.controller.MemberApiController;
import com.kgu.studywithme.member.service.MemberSignUpService;
import com.kgu.studywithme.study.controller.StudyApiController;
import com.kgu.studywithme.study.controller.StudyParticipationApiController;
import com.kgu.studywithme.study.service.ParticipationService;
import com.kgu.studywithme.study.service.StudyRegisterService;
import com.kgu.studywithme.study.service.StudyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        TokenReissueApiController.class,
        MemberApiController.class,
        OAuthApiController.class,
        CategoryApiController.class,
        StudyParticipationApiController.class,
        StudyApiController.class
})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected TokenReissueService tokenReissueService;

    @MockBean
    protected MemberSignUpService memberSignupService;

    @MockBean
    protected OAuthUri oAuthUri;

    @MockBean
    protected OAuthService oAuthService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected ParticipationService participationService;

    @MockBean
    protected StudyRegisterService studyRegisterService;

    @MockBean
    protected StudyValidator studyValidator;

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
