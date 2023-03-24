package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kgu.studywithme.member.controller.utils.SignUpRequestUtils.createSignUpRequest;
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
    @DisplayName("회원가입 API [POST /api/member]")
    class signUp {
        private static final String BASE_URL = "/api/member";

        @Test
        @DisplayName("중복되는 값(닉네임)에 의해서 회원가입에 실패한다")
        void throwExceptionByDuplicateNickname() throws Exception {
            // given
            final SignUpRequest request = createSignUpRequest();
            given(memberSignupService.signUp(any())).willThrow(StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("email").description("이메일")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("profileUrl").description("아바타 프로필 이미지 URL")
                                                    .attributes(constraint("아바타 SVG URL")),
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
            final SignUpRequest request = createSignUpRequest();
            given(memberSignupService.signUp(any())).willReturn(1L);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("이름")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("email").description("이메일")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("profileUrl").description("아바타 프로필 이미지 URL")
                                                    .attributes(constraint("아바타 SVG URL")),
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
