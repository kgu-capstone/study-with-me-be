package com.kgu.studywithme.study.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByIdAndWriterId(Long reviewId, Long memberId);
}
