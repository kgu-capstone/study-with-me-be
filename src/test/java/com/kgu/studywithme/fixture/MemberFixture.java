package com.kgu.studywithme.fixture;

import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Gender;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Password;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import static com.kgu.studywithme.common.utils.PasswordEncoderUtils.ENCODER;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("NonAsciiCharacters")
public enum MemberFixture {
    지원("서지원", "sjiwon4491@gmail.com", "abcABC123!@#", LocalDate.of(2000, 1, 18), Gender.MALE, "안양"),
    ;

    private final String name;
    private final String email;
    private final String password;
    private final LocalDate birth;
    private final Gender gender;
    private final String location;

    public Member toMember() {
        return Member.builder()
                .name(name)
                .email(Email.from(email))
                .password(Password.encrypt(password, ENCODER))
                .birth(birth)
                .phone(generateRandomPhoneNumber())
                .gender(gender)
                .location(location)
                .build();
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }
}
