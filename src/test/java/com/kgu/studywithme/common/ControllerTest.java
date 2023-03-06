package com.kgu.studywithme.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.controller.TokenReissueApiController;
import com.kgu.studywithme.auth.service.TokenReissueService;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.member.controller.MemberApiController;
import com.kgu.studywithme.member.service.MemberSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@WebMvcTest({
        TokenReissueApiController.class,
        MemberApiController.class
})
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

    protected OperationRequestPreprocessor applyRequestPreprocessor() {
        return Preprocessors.preprocessRequest(prettyPrint());
    }

    protected OperationResponsePreprocessor applyResponsePreprocessor() {
        return Preprocessors.preprocessResponse(prettyPrint());
    }

    protected String convertObjectToJson(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}
