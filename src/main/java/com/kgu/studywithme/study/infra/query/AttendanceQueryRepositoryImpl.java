package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.domain.attendance.QAttendance;
import com.kgu.studywithme.study.infra.query.dto.response.StudyWeeksDTO;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceQueryRepositoryImpl implements AttendanceQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<StudyWeeksDTO> findStudyIdAndWeekByParticipantId(Long participantId) {
        QAttendance attendance = QAttendance.attendance;
        List<Tuple> tuples = query.select(attendance.study.id, attendance.week)
                .from(attendance)
                .where(attendance.participant.id.eq(participantId))
                .fetch();
        return convertTupleToDTO(attendance, tuples);
    }

    private static List<StudyWeeksDTO> convertTupleToDTO(QAttendance attendance, List<Tuple> tuples) {
        return tuples.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(attendance.study.id),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                tuple -> tuple.get(attendance.week),
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .map(entry -> new StudyWeeksDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
