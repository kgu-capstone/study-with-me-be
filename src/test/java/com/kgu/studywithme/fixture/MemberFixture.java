package com.kgu.studywithme.fixture;

import com.kgu.studywithme.member.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    SEO_JI_WON("서지원", "서지원", "sjiwon4491@gmail.com", "profile_url", LocalDate.of(2000, 1, 18), Gender.MALE, "경기도", "안양시"),
    ;

    private final String name;
    private final String nickname;
    private final String email;
    private final String profileUrl;
    private final LocalDate birth;
    private final Gender gender;
    private final String province;
    private final String city;

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
                .build();
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }
}
