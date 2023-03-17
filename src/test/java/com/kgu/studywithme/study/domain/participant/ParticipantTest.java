package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study 도메인 {Participant Entity} 테스트")
class ParticipantTest {
    private static final Set<String> DEFAULT_HASHTAG = new HashSet<>(Set.of("A", "B", "C"));

    @Test
    @DisplayName("스터디에 참여 신청을 진행한 참여자를 생성한다")
    void constructSuccess() {
        // given
        final Study study = SPRING.toStudy(JIWON.toMember(), DEFAULT_HASHTAG);
        final Member member = GHOST.toMember();

        // when
        Participant participant = Participant.applyInStudy(study, member);

        // then
        assertAll(
                () -> assertThat(participant.getStudy()).isEqualTo(study),
                () -> assertThat(participant.getMember()).isEqualTo(member),
                () -> assertThat(participant.getStatus()).isEqualTo(APPLY)
        );
    }
}
