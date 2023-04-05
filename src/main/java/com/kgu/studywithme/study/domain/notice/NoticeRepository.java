package com.kgu.studywithme.study.domain.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // @Query
    @Query("SELECT n" +
            " FROM Notice n" +
            " JOIN FETCH n.study" +
            " WHERE n.id = :noticeId")
    Optional<Notice> findByIdWithStudy(@Param("noticeId") Long noticeId);

    // Query Method
    boolean existsByIdAndWriterId(Long noticeId, Long writerId);
}
