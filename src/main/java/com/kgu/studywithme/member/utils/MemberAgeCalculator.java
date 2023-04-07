package com.kgu.studywithme.member.utils;

import java.util.List;

public class MemberAgeCalculator {
    public static double getAverage(List<Integer> ages) {
        int sum = ages.stream()
                .mapToInt(age -> age)
                .sum();

        return (double) sum / ages.size();
    }
}
