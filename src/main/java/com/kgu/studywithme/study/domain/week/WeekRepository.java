package com.kgu.studywithme.study.domain.week;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeekRepository extends JpaRepository<Week, Long> {
    @Query("SELECT w" +
            " FROM Week w" +
            " WHERE w.study.id = :studyId AND w.week = :week")
    Optional<Week> findByStudyIdAndWeek(@Param("studyId") Long studyId, @Param("week") int week);

    @Query("SELECT w" +
            " FROM Week w" +
            " JOIN FETCH w.study" +
            " WHERE w.autoAttendance = TRUE")
    List<Week> findAutoAttendanceWeek();
}
