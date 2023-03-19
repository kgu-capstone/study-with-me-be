package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
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
                () -> assertThat(member.getGoogleProflieUrl()).isEqualTo(JIWON.getGoogleProflieUrl()),
                () -> assertThat(member.getProfileUrl()).isEqualTo(JIWON.getProfileUrl()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(JIWON.getProvince()),
                () -> assertThat(member.getRegionCity()).isEqualTo(JIWON.getCity()),
                () -> assertThat(member.getInterests()).isEqualTo(JIWON.getInterests())
        );
    }

    @Test
    @DisplayName("Member의 관심사를 수정한다")
    void updateInterests() {
        // given
        Member member = JIWON.toMember();

        // when
        final Set<Category> interests = Set.of(APTITUDE_NCS, CERTIFICATION, ETC);
        member.updateInterests(interests);

        // then
        assertThat(member.getInterests()).isEqualTo(interests);
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() {
        // given
        Member member = JIWON.toMember();

        // when
        final String change = JIWON.getNickname() + "diff";
        member.changeNickname(change);

        // then
        assertThat(member.getNicknameValue()).isEqualTo(change);
    }

    @Test
    @DisplayName("Google OAuth 통신에 의해 응답받은 사용자 프로필 이미지로 업데이트한다")
    void updateGoogleProfileUrl() {
        // given
        Member member = JIWON.toMember();

        // when
        final String googleProfileUrl = "new_google_profile_url";
        member.updateGoogleProfileUrl(googleProfileUrl);

        // then
        assertThat(member.getGoogleProflieUrl()).isEqualTo(googleProfileUrl);
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
