package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.study.infra.query.StudyCategoryQueryRepository;
import com.kgu.studywithme.study.infra.query.StudySimpleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long>,
        StudySimpleQueryRepository, StudyCategoryQueryRepository {
    // @Query
    @Query("SELECT s" +
            " FROM Study s" +
            " JOIN FETCH s.participants.host" +
            " WHERE s.id = :studyId")
    Optional<Study> findByIdWithHost(@Param("studyId") Long studyId);

    @Query("SELECT s" +
            " FROM Study s" +
            " JOIN FETCH s.participants.host" +
            " LEFT OUTER JOIN FETCH s.participants.participants" +
            " WHERE s.id = :studyId")
    Optional<Study> findByIdWithHostAndParticipant(@Param("studyId") Long studyId);

    @Query("SELECT s" +
            " FROM Study s" +
            " LEFT OUTER JOIN FETCH s.reviews.reviews r" +
            " JOIN FETCH r.writer" +
            " WHERE s.id = :studyId" +
            " ORDER BY r.modifiedAt DESC")
    Optional<Study> findByIdWithReviews(@Param("studyId") Long studyId);

    @Query("SELECT s" +
            " FROM Study s" +
            " JOIN FETCH s.participants.host h" +
            " WHERE s.id = :studyId AND h.id = :hostId")
    Optional<Study> findByIdAndHostId(@Param("studyId") Long studyId, @Param("hostId") Long hostId);

    // Query Method
    boolean existsByName(StudyName name);
    boolean existsByIdAndParticipantsHostId(Long studyId, Long hostId);
}
