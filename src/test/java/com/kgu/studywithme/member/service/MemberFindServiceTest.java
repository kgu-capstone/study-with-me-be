package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member [Service Layer] -> MemberFindService 테스트")
class MemberFindServiceTest extends ServiceTest {
    @Autowired
    private MemberFindService memberFindService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = SEO_JI_WON.toMember();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // given
        final String same = member.getEmailValue();
        final String diff = "diff" + member.getEmailValue();

        // when
        assertThatThrownBy(() -> memberFindService.findByEmail(diff))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

        Member findMember = memberFindService.findByEmail(same);

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
    }
}