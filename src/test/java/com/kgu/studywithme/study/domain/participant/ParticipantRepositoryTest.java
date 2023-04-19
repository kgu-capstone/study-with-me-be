package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Study [Repository Layer] -> ParticipantRepository 테스트")
class ParticipantRepositoryTest extends RepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));
    }

    @Test
    @DisplayName("스터디 참여 신청자를 삭제한다")
    void deleteApplier() {
        // given
        Participant participant = Participant.applyInStudy(study, member);
        participantRepository.save(participant);

        // when
        participantRepository.deleteApplier(study, member);

        // then
        Optional<Participant> findParticipant = participantRepository.findById(participant.getId());
        assertThat(findParticipant).isEmpty();
    }
}
