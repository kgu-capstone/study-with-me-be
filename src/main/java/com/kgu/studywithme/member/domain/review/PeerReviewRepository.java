package com.kgu.studywithme.member.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {
    @Query("SELECT p.content" +
            " FROM PeerReview p" +
            " WHERE p.reviewee.id = :memberId")
    List<String> findPeerReviewByMemberId(@Param("memberId") Long memberId);

    boolean existsByRevieweeIdAndReviewerId(Long revieweeId, Long reviewerId);
    Optional<PeerReview> findByRevieweeIdAndReviewerId(Long revieweeId, Long reviewerId);
}
