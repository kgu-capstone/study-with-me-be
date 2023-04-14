package com.kgu.studywithme.member.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {
    List<PeerReview> findPeerReviewByRevieweeId(@Param("revieweeId") Long revieweeId);
}
