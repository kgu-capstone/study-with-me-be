package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT a" +
            " FROM Attendance a" +
            " where a.study = :study AND a.participant = :participant AND a.week = :week")
    Optional<Attendance> findByStudyAndParticipantAndWeek(Study study, Member participant, int week);
}
