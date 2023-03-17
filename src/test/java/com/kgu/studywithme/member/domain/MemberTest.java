package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    @Test
    @DisplayName("Member를 생성한다")
    void createMember() {
        Member member = JIWON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(JIWON.getNickname()),
                () -> assertThat(member.getEmailValue()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.getProfileUrl()).isEqualTo(JIWON.getProfileUrl()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(JIWON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(JIWON.getCity())
        );
    }

    @Test
    @DisplayName("관심사와 함께 Member를 생성한다")
    void createMemberWithInterests() {
        // given
        final Set<Category> interests = Set.of(PROGRAMMING, INTERVIEW);

        // when
        Member member = JIWON.toMember();
        member.addCategoriesToInterests(interests);

        // then
        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(JIWON.getNickname()),
                () -> assertThat(member.getEmailValue()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.getProfileUrl()).isEqualTo(JIWON.getProfileUrl()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(JIWON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(JIWON.getCity()),
                () -> assertThat(member.getInterests()).containsAll(interests)
        );
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() {
        // given
        Member member = JIWON.toMember();
        final String same = JIWON.getNickname();
        final String diff = JIWON.getNickname() + "diff";

        // when
        assertThatThrownBy(() -> member.changeNickname(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());

        member.changeNickname(diff);

        // then
        assertThat(member.getNicknameValue()).isEqualTo(diff);
    }

    @Test
    @DisplayName("이메일을 통해서 동일한 사용자인지 확인한다")
    void isSameMember() {
        // given
        final Member member = JIWON.toMember();
        Member compare1 = JIWON.toMember();
        Member compare2 = GHOST.toMember();

        // when
        boolean actual1 = member.isSameMember(compare1);
        boolean actual2 = member.isSameMember(compare2);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
