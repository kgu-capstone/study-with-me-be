package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.fixture.MemberFixture.SEO_JI_WON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    @Test
    @DisplayName("멤버를 생성한다")
    void createMember() {
        Member member = SEO_JI_WON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(SEO_JI_WON.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(SEO_JI_WON.getNickname()),
                () -> assertThat(member.getEmailValue()).isEqualTo(SEO_JI_WON.getEmail()),
                () -> assertThat(member.getProfileUrl()).isEqualTo(SEO_JI_WON.getProfileUrl()),
                () -> assertThat(member.getBirth()).isEqualTo(SEO_JI_WON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(SEO_JI_WON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(SEO_JI_WON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(SEO_JI_WON.getCity())
        );
    }

    @Test
    @DisplayName("관심사와 함께 멤버를 생성한다")
    void createMemberWithInterests() {
        // given
        final Set<Category> interests = Set.of(PROGRAMMING, INTERVIEW);

        // when
        Member member = SEO_JI_WON.toMember();
        member.addCategoriesToInterests(interests);

        // then
        assertAll(
                () -> assertThat(member.getName()).isEqualTo(SEO_JI_WON.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(SEO_JI_WON.getNickname()),
                () -> assertThat(member.getEmailValue()).isEqualTo(SEO_JI_WON.getEmail()),
                () -> assertThat(member.getProfileUrl()).isEqualTo(SEO_JI_WON.getProfileUrl()),
                () -> assertThat(member.getBirth()).isEqualTo(SEO_JI_WON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(SEO_JI_WON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(SEO_JI_WON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(SEO_JI_WON.getCity()),
                () -> assertThat(member.getInterests()).containsAll(interests)
        );
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() {
        // given
        Member member = SEO_JI_WON.toMember();
        final String same = SEO_JI_WON.getNickname();
        final String diff = SEO_JI_WON.getNickname() + "diff";

        // when
        assertThatThrownBy(() -> member.changeNickname(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());

        member.changeNickname(diff);

        // then
        assertThat(member.getNicknameValue()).isEqualTo(diff);
    }
}
