package com.kgu.studywithme.study.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Attendance a" +
            " SET a.status = :status" +
            " WHERE a.study.id = :studyId AND a.week = :week AND a.participant.id IN :participantIds")
    void updateParticipantStatus(@Param("studyId") Long studyId,
                                 @Param("week") int week,
                                 @Param("participantIds") Set<Long> participantIds,
                                 @Param("status") AttendanceStatus status);

    // Query Method
    Optional<Attendance> findByStudyIdAndParticipantIdAndWeek(Long studyId, Long participantId, int week);
}
