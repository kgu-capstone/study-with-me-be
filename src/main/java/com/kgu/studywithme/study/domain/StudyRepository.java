package com.kgu.studywithme.study.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    @Query("SELECT s" +
            " FROM Study s" +
            " JOIN FETCH s.participants.host" +
            " WHERE s.id = :studyId")
    Optional<Study> findByIdWithHost(@Param("studyId") Long studyId);

    @Query("SELECT s" +
            " FROM Study s" +
            " JOIN FETCH s.participants.host h" +
            " WHERE s.id = :studyId AND h.id = :hostId")
    Optional<Study> findByIdAndHostId(@Param("studyId") Long studyId, @Param("hostId") Long hostId);

    boolean existsByName(StudyName name);
}
