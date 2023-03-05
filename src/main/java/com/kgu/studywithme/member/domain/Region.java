package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Region {
    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "city", nullable = false)
    private String city;

    private Region(String province, String city) {
        this.province = province;
        this.city = city;
    }

    public static Region of(String province, String city) {
        validateInputRegion(province, city);
        return new Region(province, city);
    }

    private static void validateInputRegion(String province, String city) {
        if (isEmptyText(province) || isEmptyText(city)) {
            throw StudyWithMeException.type(MemberErrorCode.INVALID_REGION);
        }
    }

    private static boolean isEmptyText(String str) {
        return !StringUtils.hasText(str);
    }
}
