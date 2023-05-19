package com.kgu.studywithme.member.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record MemberUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "전화번호는 필수입니다.")
        String phone,

        @NotBlank(message = "거주지는 필수입니다.")
        String province,

        @NotBlank(message = "거주지는 필수입니다.")
        String city,

        @NotNull(message = "이메일 수신 동의 여부는 필수입니다.")
        Boolean emailOptIn,

        @NotEmpty(message = "관심사는 하나 이상 등록해야 합니다.")
        Set<Long> categories
) {
}
