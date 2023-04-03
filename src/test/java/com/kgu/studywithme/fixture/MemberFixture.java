package com.kgu.studywithme.fixture;

import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.service.dto.response.LoginResponse;
import com.kgu.studywithme.auth.service.dto.response.MemberInfo;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    JIWON("서지원", "서지원", "sjiwon4491@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    GHOST("고스트", "고스트", "ghost@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(APTITUDE_NCS, CERTIFICATION, ETC))),
    ANONYMOUS("익명", "익명", "anonymous@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(APTITUDE_NCS, CERTIFICATION, ETC))),


    DUMMY1("더미1", "더미1", "dummy1@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY2("더미2", "더미2", "dummy2@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY3("더미3", "더미3", "dummy3@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY4("더미4", "더미4", "dummy4@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY5("더미5", "더미5", "dummy5@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY6("더미6", "더미6", "dummy6@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY7("더미7", "더미7", "dummy7@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY8("더미8", "더미8", "dummy8@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    DUMMY9("더미9", "더미9", "dummy9@gmail.com", LocalDate.of(2000, 1, 18),
            Gender.MALE, "경기도", "안양시", new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))),
    ;

    private final String name;
    private final String nickname;
    private final String email;
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
        Member member = this.toMember();
        ReflectionTestUtils.setField(member, "id", 1L);

        return LoginResponse.builder()
                .member(MemberInfo.from(member))
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return GoogleUserResponse.builder()
                .name(name)
                .email(email)
                .picture("google_profile_url")
                .build();
    }
}
