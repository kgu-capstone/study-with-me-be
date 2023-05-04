package com.kgu.studywithme.study.domain.notice.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.notice.id = :noticeId")
    void deleteByNoticeId(@Param("noticeId") Long noticeId);

    // Query Method
    boolean existsByIdAndWriterId(Long commentId, Long memberId);
}
