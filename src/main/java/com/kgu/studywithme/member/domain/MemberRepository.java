package com.kgu.studywithme.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(Email email);
    boolean existsByNickname(Nickname nickname);
    boolean existsByPhone(String phone);
}
