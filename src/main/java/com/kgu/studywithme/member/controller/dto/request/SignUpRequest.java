package com.kgu.studywithme.member.controller.dto.request;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.*;
import com.kgu.studywithme.member.utils.validator.ValidGender;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kgu.studywithme.member.domain.Gender.FEMALE;
import static com.kgu.studywithme.member.domain.Gender.MALE;

public record SignUpRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotNull(message = "생년월일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,

        @NotBlank(message = "전화번호는 필수입니다.")
        String phone,

        @ValidGender
        @NotBlank(message = "성별은 필수입니다.")
        String gender,

        @NotBlank(message = "거주지는 필수입니다.")
        String province,

        @NotBlank(message = "거주지는 필수입니다.")
        String city,

        @NotNull(message = "이메일 수신 동의 여부는 필수입니다.")
        Boolean emailOptIn,

        @NotEmpty(message = "관심사는 하나 이상 등록해야 합니다.")
        List<Long> categories
) {
    public Member toEntity() {
        return Member.createMember(
                name,
                Nickname.from(nickname),
                Email.from(email),
                birth,
                phone,
                convertStringToGender(),
                Region.of(province, city),
                emailOptIn,
                convertStringToCategory()
        );
    }

    private Gender convertStringToGender() {
        return gender.equalsIgnoreCase("M") ? MALE : FEMALE;
    }

    private Set<Category> convertStringToCategory() {
        return categories.stream()
                .map(Category::from)
                .collect(Collectors.toSet());
    }
}
