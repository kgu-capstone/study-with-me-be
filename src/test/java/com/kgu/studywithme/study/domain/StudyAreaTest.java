package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study 도메인 {StudyArea VO} 테스트")
class StudyAreaTest {
    @ParameterizedTest(name = "{index}: {0} - {1}")
    @MethodSource("validArea")
    @DisplayName("StudyArea[province / city / detail]를 생성한다")
    void constructSuccess(String province, String city) {
        StudyArea area = StudyArea.of(province, city);
        assertAll(
                () -> assertThat(area.getProvince()).isEqualTo(province),
                () -> assertThat(area.getCity()).isEqualTo(city)
        );
    }

    private static Stream<Arguments> validArea() {
        return Stream.of(
                Arguments.of("경기도", "안양시"),
                Arguments.of("경기도", "수원시"),
                Arguments.of("경기도", "성남시")
        );
    }

    @ParameterizedTest(name = "{index}: {0} - {1}")
    @MethodSource("invalidArea")
    @DisplayName("province나 city나 detail이 비어있음에 따라 StudyArea 생성에 실패한다")
    void constructFailure(String province, String city) {
        assertThatThrownBy(() -> StudyArea.of(province, city))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_AREA_IS_BLANK.getMessage());
    }

    private static Stream<Arguments> invalidArea() {
        return Stream.of(
                Arguments.of("경기도", ""),
                Arguments.of("", "안양시"),
                Arguments.of("", "")
        );
    }
}
