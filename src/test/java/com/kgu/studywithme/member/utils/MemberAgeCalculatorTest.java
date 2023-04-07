package com.kgu.studywithme.member.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member [Utils] -> MemberAgeCalculator 테스트")
class MemberAgeCalculatorTest {
    @Test
    @DisplayName("스터디 팀원들의 평균 나이를 계산한다")
    void getAverage() {
        // given
        List<Integer> list = List.of(20, 21, 23, 25, 26, 28, 30);
        final int sum = list.stream().mapToInt(integer -> integer).sum();
        final double avg = (double) sum / list.size();

        // when
        double result = MemberAgeCalculator.getAverage(list);

        // then
        assertThat(result).isEqualTo(avg);
    }
}
