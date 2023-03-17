package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study 도메인 {Participant Entity} 테스트")
class ParticipantTest {
    @Test
    @DisplayName("스터디에 참여 신청을 진행한 사용자를 생성한다")
    void constructSuccess() {
        // given
        final Study study = SPRING.toStudy(JIWON.toMember());
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

    @ParameterizedTest
    @MethodSource("provideForIsSameParticipant")
    @DisplayName("동일한 참여자인지 확인한다")
    void isSameParticipant(Member member, Member target, boolean expected) {
        Study study = SPRING.toStudy(JIWON.toMember());
        Participant participant = Participant.applyInStudy(study, member);

        assertThat(participant.isSameMember(target)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsSameParticipant() {
        return Stream.of(
                Arguments.of(JIWON.toMember(), JIWON.toMember(), true),
                Arguments.of(JIWON.toMember(), GHOST.toMember(), false)
        );
    }
}
