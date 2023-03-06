package com.kgu.studywithme.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(Email email);
    boolean existsByEmail(Email email);
    boolean existsByNickname(Nickname nickname);
    boolean existsByPhone(String phone);
}
