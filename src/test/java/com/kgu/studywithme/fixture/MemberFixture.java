package com.kgu.studywithme.fixture;

import com.kgu.studywithme.member.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    SEO_JI_WON("서지원", "sjiwon4491@gmail.com", "abcABC123!@#", LocalDate.of(2000, 1, 18), Gender.MALE, "경기도", "안양시"),
    ;

    private final String name;
    private final String email;
    private final String password;
    private final LocalDate birth;
    private final Gender gender;
    private final String province;
    private final String city;

    public Member toMember() {
        return Member.builder()
                .name(name)
                .email(Email.from(email))
                .password(Password.encrypt(password, ENCODER))
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
