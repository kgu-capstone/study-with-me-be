package com.kgu.studywithme.study.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // @Query
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Attendance a" +
            " SET a.status = :status" +
            " WHERE a.participant.id = :participantId AND a.week = :week")
    void applyParticipantAttendanceStatus(@Param("participantId") Long participantId,
                                          @Param("week") int week,
                                          @Param("status") AttendanceStatus status);

    // Query Method
    Optional<Attendance> findByStudyIdAndParticipantIdAndWeek(Long studyId, Long participantId, int week);
}
