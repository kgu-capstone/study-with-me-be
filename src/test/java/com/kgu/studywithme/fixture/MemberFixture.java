package com.kgu.studywithme.fixture;

import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.service.dto.response.LoginResponse;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    JIWON("서지원", "서지원", "sjiwon4491@gmail.com", "profile_url", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    GHOST("고스트", "고스트", "ghost@gmail.com", "profile_url", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(APTITUDE_NCS, CERTIFICATION, ETC))),
    ANONYMOUS("익명", "익명", "anonymous@gmail.com", "profile_url", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(APTITUDE_NCS, CERTIFICATION, ETC))),
    ;

    private final String name;
    private final String nickname;
    private final String email;
    private final String profileUrl;
    private final LocalDate birth;
    private final Gender gender;
    private final String province;
    private final String city;
    private final Set<Category> interests;

    public Member toMember() {
        return Member.builder()
                .name(name)
                .nickname(Nickname.from(nickname))
                .email(Email.from(email))
                .profileUrl(profileUrl)
                .birth(birth)
                .phone(generateRandomPhoneNumber())
                .gender(gender)
                .region(Region.of(province, city))
                .interests(interests)
                .build();
    }

    private static String generateRandomPhoneNumber() {
        String first = "010";
        String second = String.valueOf((int) (Math.random() * 9000 + 1000));
        String third = String.valueOf((int) (Math.random() * 9000 + 1000));
        return String.join("-", first, second, third);
    }

    public LoginResponse toLoginResponse() {
        return LoginResponse.builder()
                .userInfo(toGoogleUserResponse())
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return GoogleUserResponse.builder()
                .name(name)
                .email(email)
                .picture(profileUrl)
                .build();
    }
}
