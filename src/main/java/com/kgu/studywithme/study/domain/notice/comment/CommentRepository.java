package com.kgu.studywithme.study.domain.notice.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteAllByNoticeId(Long noticeId);
    boolean existsByIdAndWriterId(Long commentId, Long memberId);
}