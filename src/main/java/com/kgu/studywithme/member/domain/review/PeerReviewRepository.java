package com.kgu.studywithme.member.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {
    @Query("SELECT r.content" +
            " FROM PeerReview r" +
            " WHERE r.reviewee.id = :memberId")
    List<String> findContentByRevieweeId(@Param("memberId") Long memberId);
}
