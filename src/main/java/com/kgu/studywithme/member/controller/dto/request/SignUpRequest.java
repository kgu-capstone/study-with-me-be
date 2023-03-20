package com.kgu.studywithme.member.controller.dto.request;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.*;
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
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "구글 프로필 URL은 필수입니다.")
        String googleProfileUrl,

        @NotBlank(message = "프로필 URL은 필수입니다.")
        String profileUrl,

        @NotNull(message = "생년월일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,

        @NotBlank(message = "전화번호는 필수입니다.")
        String phone,

        @NotBlank(message = "성별은 필수입니다.")
        String gender,

        @NotBlank(message = "거주지는 필수입니다.")
        String province,

        @NotBlank(message = "거주지는 필수입니다.")
        String city,

        @NotEmpty(message = "관심사는 하나 이상 등록해야 합니다.")
        List<Long> categories
) {
    @Builder
    public SignUpRequest {}

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .nickname(Nickname.from(nickname))
                .email(Email.from(email))
                .googleProflieUrl(googleProfileUrl)
                .profileUrl(RealProfile.from(profileUrl))
                .birth(birth)
                .phone(phone)
                .gender(convertStringToGender())
                .region(Region.of(province, city))
                .interests(convertStringToCategory())
                .build();
    }

    private Gender convertStringToGender() {
        if ("M".equalsIgnoreCase(gender)) {
            return MALE;
        } else if ("F".equalsIgnoreCase(gender)) {
            return FEMALE;
        } else {
            throw StudyWithMeException.type(GlobalErrorCode.VALIDATION_ERROR);
        }
    }

    private Set<Category> convertStringToCategory() {
        return categories.stream()
                .map(Category::from)
                .collect(Collectors.toSet());
    }
}
