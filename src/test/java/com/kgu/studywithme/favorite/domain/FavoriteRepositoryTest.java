package com.kgu.studywithme.favorite.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite [Repository Layer] -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member member;
    private Study study1;
    private Study study2;

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());

        member = memberRepository.save(GHOST.toMember());
        study1 = studyRepository.save(TOEIC.toOnlineStudy(host));
        study2 = studyRepository.save(SPRING.toOnlineStudy(host));
    }

    @Test
    @DisplayName("특정 스터디에 대해서 사용자가 찜을 했는지 여부를 확인한다")
    void exists() {
        // given
        favoriteRepository.save(Favorite.favoriteMarking(study1.getId(), member.getId()));

        // when
        boolean actual1 = favoriteRepository.existsByStudyIdAndMemberId(study1.getId(), member.getId());
        boolean actual2 = favoriteRepository.existsByStudyIdAndMemberId(study2.getId(), member.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("특정 스터디에 대한 사용자 찜 현황을 삭제한다")
    void delete() {
        // given
        favoriteRepository.save(Favorite.favoriteMarking(study1.getId(), member.getId()));

        // when
        favoriteRepository.deleteByStudyIdAndMemberId(study1.getId(), member.getId());

        // then
        assertThat(favoriteRepository.existsByStudyIdAndMemberId(study1.getId(), member.getId())).isFalse();
    }
}
