package com.kgu.studywithme.member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member 도메인 {Score VO} 테스트")
class ScoreTest {
    @Test
    @DisplayName("Score를 생성한다")
    void construct() {
        Score score = Score.initScore();
        
        assertThat(score.getValue()).isEqualTo(100);
    }
    
    @Test
    @DisplayName("Maximum을 넘어선 Score를 설정할 경우 Maximum으로 변경된다")
    void maximum() {
        // given
        Score score = Score.initScore(); // 100
        
        // when
        Score updateScore = score.applyAttendance();// 100 + 1

        // then
        assertThat(updateScore.getValue()).isEqualTo(100);
    }
    
    @Test
    @DisplayName("Minimum보다 낮은 Score를 설정할 경우 Minimum으로 변경된다")
    void minimum() {
        // given
        Score score = Score.from(0); // 100

        // when
        Score updateScore = score.applyLate(); // 0 - 1
        
        // then
        assertThat(updateScore.getValue()).isEqualTo(0);
    }

    @Nested
    @DisplayName("단순 출석에 대한 Score 업데이트")
    class applySimpleAttendance {
        private Score score;

        @BeforeEach
        void setUp() {
            score = Score.from(75); // 75
        }

        @Test
        @DisplayName("출석에 대한 Score를 업데이트한다")
        void attendance() {
            // when
            Score updateScore = score.applyAttendance(); // 75 + 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(76);
        }

        @Test
        @DisplayName("지각에 대한 Score를 업데이트한다")
        void late() {
            // when
            Score updateScore = score.applyLate(); // 75 - 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(74);
        }

        @Test
        @DisplayName("결석에 대한 Score를 업데이트한다")
        void absence() {
            // when
            Score updateScore = score.applyAbsence(); // 75 - 5

            // then
            assertThat(updateScore.getValue()).isEqualTo(70);
        }
    }

    @Nested
    @DisplayName("이전 출석 정보 수정에 따른 Score 업데이트")
    class applyComplexAttendance {
        private Score score;

        @BeforeEach
        void setUp() {
            score = Score.from(75); // 75
        }

        @Test
        @DisplayName("출석 -> 지각으로 수정함에 따라 Score를 업데이트한다")
        void updateAttendanceToLate() {
            // when
            Score updateScore = score.updateAttendanceToLate(); // 75 - 1 - 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(73);
        }

        @Test
        @DisplayName("출석 -> 결석으로 수정함에 따라 Score를 업데이트한다")
        void updateAttendanceToAbsence() {
            // when
            Score updateScore = score.updateAttendanceToAbsence(); // 75 - 1 - 5

            // then
            assertThat(updateScore.getValue()).isEqualTo(69);
        }

        @Test
        @DisplayName("지각 -> 출석으로 수정함에 따라 Score를 업데이트한다")
        void updateLateToAttendance() {
            // when
            Score updateScore = score.updateLateToAttendance(); // 75 + 1 + 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(77);
        }

        @Test
        @DisplayName("지각 -> 결석으로 수정함에 따라 Score를 업데이트한다")
        void updateLateToAbsence() {
            // when
            Score updateScore = score.updateLateToAbsence(); // 75 + 1 - 5

            // then
            assertThat(updateScore.getValue()).isEqualTo(71);
        }

        @Test
        @DisplayName("결석 -> 출석으로 수정함에 따라 Score를 업데이트한다")
        void updateAbsenceToAttendance() {
            // when
            Score updateScore = score.updateAbsenceToAttendance(); // 75 + 5 + 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(81);
        }

        @Test
        @DisplayName("결석 -> 지각으로 수정함에 따라 Score를 업데이트한다")
        void updateAbsenceToLate() {
            // when
            Score updateScore = score.updateAbsenceToLate(); // 75 + 5 - 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(79);
        }
    }
}
