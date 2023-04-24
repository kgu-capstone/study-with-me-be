package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.domain.StudyThumbnail.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study 도메인 {StudyThumbnail Enum} 테스트")
class StudyThumbnailTest {
    @Test
    @DisplayName("특정 스터디 썸네일을 조회한다")
    void findSpecificStudyImage() {
        // given
        final String language = "language_001.png";
        final String interview = "interview_001.png";
        final String programming = "programming_001.png";
        final String aptituteAndNcs = "aptitude_ncs_001.png";
        final String certification = "certification_001.png";
        final String etc = "etc_001.png";

        // when - then
        assertAll(
                () -> assertThat(StudyThumbnail.from(language)).isEqualTo(IMAGE_LANGUAGE_001),
                () -> assertThat(StudyThumbnail.from(interview)).isEqualTo(IMAGE_INTERVIEW_001),
                () -> assertThat(StudyThumbnail.from(programming)).isEqualTo(IMAGE_PROGRAMMING_001),
                () -> assertThat(StudyThumbnail.from(aptituteAndNcs)).isEqualTo(IMAGE_APTITUDE_NCS_001),
                () -> assertThat(StudyThumbnail.from(certification)).isEqualTo(IMAGE_CERTIFICATION_001),
                () -> assertThat(StudyThumbnail.from(etc)).isEqualTo(IMAGE_ETC_001)
        );
    }

    @Test
    @DisplayName("제공해주지 않는 썸네일을 조회하면 예외가 발생한다")
    void throwExceptionByfindAnonymousStudyImage() {
        // given
        final String anonymous = "language_001010101239012321321.png";

        // when - then
        assertThatThrownBy(() -> StudyThumbnail.from(anonymous))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_THUMBNAIL_NOT_FOUND.getMessage());
    }
}
