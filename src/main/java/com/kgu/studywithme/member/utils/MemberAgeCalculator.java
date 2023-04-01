package com.kgu.studywithme.member.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class MemberAgeCalculator {
    public static double getAverage(List<LocalDate> birthList) {
        int sum = birthList.stream()
                .mapToInt(birth -> Period.between(birth, LocalDate.now()).getYears())
                .sum();

        return (double) sum / birthList.size();
    }
}
