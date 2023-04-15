package com.kgu.studywithme.study.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByStudyIdAndParticipantIdAndWeek(Long studyId, Long participantId, int week);

    @Query("SELECT a.week" +
            " FROM Attendance a" +
            " WHERE a.study.id = :studyId AND a.participant.id = :participantId")
    Set<Integer> findWeekByStudyIdAndParticipantId(@Param("studyId") Long studyId, @Param("participantId") Long participantId);

    @Query("SELECT DISTINCT a.study.id" +
            " FROM Attendance a" +
            " WHERE a.participant.id = :participantId")
    Set<Long> findStudyIdByParticipantId(@Param("participantId") Long participantId);
}
