package com.kgu.studywithme.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Favorite f WHERE f.studyId = :studyId AND f.memberId = :memberId")
    void deleteByStudyIdAndMemberId(@Param("studyId") Long studyId, @Param("memberId") Long memberId);

    // Query Method
    boolean existsByStudyIdAndMemberId(Long studyId, Long memberId);
}
