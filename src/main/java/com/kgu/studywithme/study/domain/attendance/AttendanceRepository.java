package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.study.infra.query.AttendanceQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceQueryRepository {
    Optional<Attendance> findByStudyIdAndParticipantIdAndWeek(Long studyId, Long participantId, int week);
}
