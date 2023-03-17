package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Repository Layer] -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("이메일에 해당하는 사용자가 존재하는지 확인한다")
    void existsByEmail() {
        // given
        final Email same = member.getEmail();
        final Email diff = Email.from("diff" + member.getEmailValue());

        // when
        boolean actual1 = memberRepository.existsByEmail(same);
        boolean actual2 = memberRepository.existsByEmail(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("닉네임에 해당하는 사용자가 존재하는지 확인한다")
    void existsByNickname() {
        // given
        final Nickname same = member.getNickname();
        final Nickname diff = Nickname.from("diff" + member.getNicknameValue());

        // when
        boolean actual1 = memberRepository.existsByNickname(same);
        boolean actual2 = memberRepository.existsByNickname(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("전화번호에 해당하는 사용자가 존재하는지 확인한다")
    void existsByPhone() {
        // given
        final String same = member.getPhone();
        final String diff = member.getPhone().replaceAll("0", "9");

        // when
        boolean actual1 = memberRepository.existsByPhone(same);
        boolean actual2 = memberRepository.existsByPhone(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // given
        final Email same = member.getEmail();
        final Email diff = Email.from("diff" + member.getEmailValue());

        // when
        Optional<Member> findMember1 = memberRepository.findByEmail(same);
        Optional<Member> findMember2 = memberRepository.findByEmail(diff);

        // then
        assertAll(
                () -> assertThat(findMember1).isPresent(),
                () -> assertThat(findMember2).isEmpty()
        );
    }
}
