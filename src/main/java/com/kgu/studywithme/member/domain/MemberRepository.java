package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.member.infra.query.MemberSimpleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberSimpleQueryRepository {
    // @Query
    @Query("SELECT m" +
            " FROM Member m" +
            " JOIN FETCH m.interests" +
            " WHERE m.id = :memberId")
    Optional<Member> findByIdWithInterests(@Param("memberId") Long memberId);

    // Query Method
    Optional<Member> findByEmail(Email email);
    boolean existsByEmail(Email email);
    boolean existsByNickname(Nickname nickname);
    boolean existsByPhone(String phone);
}
