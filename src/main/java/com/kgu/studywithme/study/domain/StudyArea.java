package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class StudyArea {
    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    private StudyArea(String province, String city) {
        this.province = province;
        this.city = city;
    }

    public static StudyArea of(String province, String city) {
        validateProvinceAndCityIsNotEmpty(province, city);
        return new StudyArea(province, city);
    }

    private static void validateProvinceAndCityIsNotEmpty(String province, String city) {
        if (isEmptyText(province) || isEmptyText(city)) {
            throw StudyWithMeException.type(StudyErrorCode.STUDY_AREA_IS_BLANK);
        }
    }

    private static boolean isEmptyText(String str) {
        return !StringUtils.hasText(str);
    }
}
