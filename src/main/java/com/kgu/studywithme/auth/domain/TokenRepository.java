package com.kgu.studywithme.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t" +
            " SET t.refreshToken = :refreshToken" +
            " WHERE t.memberId = :memberId")
    void reissueRefreshTokenByRtrPolicy(@Param("memberId") Long memberId, @Param("refreshToken") String newRefreshToken);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Token t" +
            " WHERE t.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    // Query Method
    Optional<Token> findByMemberId(Long memberId);
    boolean existsByMemberIdAndRefreshToken(Long memberId, String refreshToken);
}
