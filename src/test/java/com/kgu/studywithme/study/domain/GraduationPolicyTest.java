package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study 도메인 {GraduationPolicy VO} 테스트")
class GraduationPolicyTest {
    @Test
    @DisplayName("GraduationPolicy를 생성한다")
    void construct() {
        GraduationPolicy policy = GraduationPolicy.initPolicy(10);

        assertAll(
                () -> assertThat(policy.getMinimumAttendance()).isEqualTo(10),
                () -> assertThat(policy.getUpdateChance()).isEqualTo(3)
        );
    }

    @Nested
    @DisplayName("GraduationPoicy 수정")
    class update {
        private GraduationPolicy policy;

        @BeforeEach
        void setUp() {
            policy = GraduationPolicy.initPolicy(10);
        }

        @Test
        @DisplayName("수정할 기회가 남아있지 않음에 따라 GraduationPolicy를 수정할 수 없다")
        void throwExceptionByNoChangeToUpdateGraduationPolicy() {
            // given
            ReflectionTestUtils.setField(policy, "updateChance", 0);

            // when - then
            assertThatThrownBy(() -> policy.update(20))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.NO_CHANGE_TO_UPDATE_GRADUATION_POLICY.getMessage());
        }

        @Test
        @DisplayName("GraduationPolicy 수정에 성공한다 [변화 X]")
        void success1() {
            // when
            GraduationPolicy update = policy.update(10);

            // then
            assertAll(
                    () -> assertThat(update.getMinimumAttendance()).isEqualTo(10),
                    () -> assertThat(update.getUpdateChance()).isEqualTo(3)
            );
        }

        @Test
        @DisplayName("GraduationPolicy 수정에 성공한다 [변화 O]")
        void success2() {
            // when
            GraduationPolicy update = policy.update(20);

            // then
            assertAll(
                    () -> assertThat(update.getMinimumAttendance()).isEqualTo(20),
                    () -> assertThat(update.getUpdateChance()).isEqualTo(2)
            );
        }
    }

    @Test
    @DisplayName("스터디 팀장 위임 후 GraduationPolicy 수정 기회를 초기화한다")
    void resetForDelegatingStudyHost() {
        // given
        GraduationPolicy policy = GraduationPolicy
                .initPolicy(10) // remain = 3
                .update(15) // remain = 2
                .update(13); // remain = 1

        // when
        GraduationPolicy resetUpdateChange = policy.resetUpdateChanceForDelegatingStudyHost();

        // then
        assertAll(
                () -> assertThat(resetUpdateChange.getMinimumAttendance()).isEqualTo(13),
                () -> assertThat(resetUpdateChange.getUpdateChance()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("졸업 요건을 충족했는지 여부를 확인한다")
    void isGraduationRequirementsFulfilled() {
        // given
        GraduationPolicy policy = GraduationPolicy.initPolicy(10);

        // when
        boolean actual1 = policy.isGraduationRequirementsFulfilled(9);
        boolean actual2 = policy.isGraduationRequirementsFulfilled(11);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
