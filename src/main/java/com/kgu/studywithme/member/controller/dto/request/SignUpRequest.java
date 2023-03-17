package com.kgu.studywithme.member.controller.dto.request;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.*;
import com.kgu.studywithme.member.exception.MemberRequestValidationMessage;
import lombok.Builder;
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
        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_NAME)
        String name,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_NICKNAME)
        String nickname,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_EMAIL)
        String email,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_PROFILE_URL)
        String profileUrl,

        @NotNull(message = MemberRequestValidationMessage.SignUp.MEMBER_BIRTH)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_PHONE)
        String phone,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_GENDER)
        String gender,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_REGION)
        String province,

        @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_REGION)
        String city,

        @NotEmpty(message = MemberRequestValidationMessage.SignUp.MEMBER_INTERESTS)
        List<Long> categories
) {
    @Builder
    public SignUpRequest {}

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .nickname(Nickname.from(nickname))
                .email(Email.from(email))
                .profileUrl(profileUrl)
                .birth(birth)
                .phone(phone)
                .gender(convertStringToGender(gender))
                .region(Region.of(province, city))
                .build();
    }

    private Gender convertStringToGender(String gender) {
        if ("M".equalsIgnoreCase(gender)) {
            return MALE;
        } else if ("F".equalsIgnoreCase(gender)) {
            return FEMALE;
        } else {
            throw StudyWithMeException.type(GlobalErrorCode.VALIDATION_ERROR);
        }
    }

    public Set<Category> toInterestCategories() {
        return categories.stream()
                .map(Category::from)
                .collect(Collectors.toSet());
    }
}
