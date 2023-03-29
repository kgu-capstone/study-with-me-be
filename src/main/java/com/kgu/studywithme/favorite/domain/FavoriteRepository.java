package com.kgu.studywithme.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findFavoriteByStudyIdAndMemberId(Long studyId, Long memberId);
    boolean existsByStudyIdAndMemberId(Long studyId, Long memberId);

    void deleteByStudyIdAndMemberId(Long studyId, Long memberId);
}