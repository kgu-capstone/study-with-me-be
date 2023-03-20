package com.kgu.studywithme.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class RealProfile {
    private static final String AVATAR_PREFIX = "https://source.boringavatars.com";

    @Column(name = "profile_url", nullable = false)
    private String value;

    private RealProfile(String value) {
        this.value = value;
    }

    public static RealProfile from(String value) {
        return new RealProfile(value);
    }

    public RealProfile updateProfileUrl(String profileUrl) {
        return new RealProfile(profileUrl);
    }

    public boolean isAvatarProfile() {
        return value.startsWith(AVATAR_PREFIX);
    }
}
