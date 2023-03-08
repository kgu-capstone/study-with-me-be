package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import com.kgu.studywithme.member.controller.utils.SignUpRequestUtils;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberApiController 테스트")
class MemberApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원가입 API 테스트 [POST /api/member]")
    class signUp {
        private static final String BASE_URL = "/api/member";

        @Test
        @DisplayName("중복되는 값(닉네임)에 의해서 회원가입에 실패한다")
        void throwExceptionByDuplicateNickname() throws Exception {
            // given
            final SignUpRequest request = SignUpRequestUtils.createRequest();
            given(memberSignupService.signUp(any(), any())).willThrow(StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.status").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("email").description("이메일")
                                                    .attributes(constraint("@gmail.com")),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("birth").description("생년월일"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("gender").description("성별")
                                                    .attributes(constraint("M / F")),
                                            fieldWithPath("province").description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city").description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("categories").description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
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
        @DisplayName("회원가입에 성공한다")
        void success() throws Exception {
            // given
            final SignUpRequest request = SignUpRequestUtils.createRequest();
            given(memberSignupService.signUp(any(), any())).willReturn(1L);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("email").description("이메일")
                                                    .attributes(constraint("@gmail.com")),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("birth").description("생년월일"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("gender").description("성별")
                                                    .attributes(constraint("M / F")),
                                            fieldWithPath("province").description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city").description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("categories").description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
                                    )
                            )
                    );
        }
    }
}
