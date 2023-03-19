package com.kgu.studywithme.member.controller.utils;

import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;

public class SignUpRequestUtils {
    private static final String MALE = "M";
    private static final List<Long> CATEGORIES = List.of(LANGUAGE.getId(), INTERVIEW.getId(), PROGRAMMING.getId());

    public static SignUpRequest createSignUpRequest() {
        return SignUpRequest.builder()
                .name(JIWON.getName())
                .nickname(JIWON.getNickname())
                .email(JIWON.getEmail())
                .googleProfileUrl(JIWON.getGoogleProflieUrl())
                .profileUrl(JIWON.getProfileUrl())
                .birth(JIWON.getBirth())
                .phone(generateRandomPhoneNumber())
                .gender(MALE)
                .province(JIWON.getProvince())
                .city(JIWON.getCity())
                .categories(CATEGORIES)
                .build();
    }

    private static String generateRandomPhoneNumber() {
        String first = "010";
        String second = String.valueOf((int) (Math.random() * 9000 + 1000));
        String third = String.valueOf((int) (Math.random() * 9000 + 1000));
        return String.join("-", first, second, third);
    }
}
