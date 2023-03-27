package com.kgu.studywithme.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findFavoriteByStudyIdAndMemberId(Long studyId, Long memberId);
    boolean existsByStudyIdAndMemberId(Long studyId, Long memberId);
}
