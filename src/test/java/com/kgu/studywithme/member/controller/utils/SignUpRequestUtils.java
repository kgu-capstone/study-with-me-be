package com.kgu.studywithme.member.controller.utils;

import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;

public class SignUpRequestUtils {
    private static final String MALE = "M";
    private static final String FEMALE = "F";
    private static final List<Long> CATEGORIES = List.of(LANGUAGE.getId(), INTERVIEW.getId(), PROGRAMMING.getId());

    public static SignUpRequest createRequest() {
        return SignUpRequest.builder()
                .name(SEO_JI_WON.getName())
                .nickname(SEO_JI_WON.getNickname())
                .email(SEO_JI_WON.getEmail())
                .profileUrl(SEO_JI_WON.getProfileUrl())
                .birth(SEO_JI_WON.getBirth())
                .phone(generateRandomPhoneNumber())
                .gender(MALE)
                .province(SEO_JI_WON.getProvince())
                .city(SEO_JI_WON.getCity())
                .categories(CATEGORIES)
                .build();
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }
}
