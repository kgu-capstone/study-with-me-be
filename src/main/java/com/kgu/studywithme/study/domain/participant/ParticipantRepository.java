package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Participant p WHERE p.study = :study AND p.member = :member")
    void deleteApplier(@Param("study") Study study, @Param("member") Member member);
}
