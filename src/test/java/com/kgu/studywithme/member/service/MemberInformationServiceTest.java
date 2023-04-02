package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberInformationService 테스트")
class MemberInformationServiceTest extends ServiceTest {
    @Autowired
    private MemberInformationService memberInformationService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("사용자의 기본 정보를 조회한다")
    void getInformation() {
        // when
        MemberInformation information = memberInformationService.getInformation(member.getId());

        // then
        assertAll(
                () -> assertThat(information.id()).isEqualTo(member.getId()),
                () -> assertThat(information.name()).isEqualTo(member.getName()),
                () -> assertThat(information.nickname()).isEqualTo(member.getNicknameValue()),
                () -> assertThat(information.email()).isEqualTo(member.getEmailValue()),
                () -> assertThat(information.birth()).isEqualTo(member.getBirth()),
                () -> assertThat(information.phone()).isEqualTo(member.getPhone()),
                () -> assertThat(information.gender()).isEqualTo(member.getGender().getValue()),
                () -> assertThat(information.region()).isEqualTo(member.getRegion()),
                () -> assertThat(information.interests()).containsAll(List.of(LANGUAGE.getName(), INTERVIEW.getName(), PROGRAMMING.getName()))
        );
    }
}
