package com.kgu.studywithme.favorite.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite [Service Layer] -> FavoriteManageService 테스트")
class FavoriteManageServiceTest extends ServiceTest {
    @Autowired
    private FavoriteManageService favoriteManageService;

    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        study = studyRepository.save(TOEIC.toOnlineStudy(host));
    }

    @Nested
    @DisplayName("찜 등록")
    class like {
        @Test
        @DisplayName("이미 찜 등록된 스터디를 찜할 수 없다")
        void throwExceptionByAlreadyFavoriteMarked() {
            // given
            favoriteManageService.like(study.getId(), member.getId());

            // when - then
            assertThatThrownBy(() -> favoriteManageService.like(study.getId(), member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.ALREADY_FAVORITE_MARKED.getMessage());
        }

        @Test
        @DisplayName("찜 등록에 성공한다")
        void success() {
            // when
            Long favoriteId = favoriteManageService.like(study.getId(), member.getId());

            // then
            Favorite favorite = favoriteRepository.findById(favoriteId).orElseThrow();
            assertAll(
                    () -> assertThat(favorite.getStudyId()).isEqualTo(study.getId()),
                    () -> assertThat(favorite.getMemberId()).isEqualTo(member.getId())
            );
        }
    }

    @Nested
    @DisplayName("찜 취소")
    class cancel {
        @Test
        @DisplayName("찜 등록이 되지 않은 스터디를 취소할 수 없다")
        void throwExceptionByNotFavoriteMarked() {
            assertThatThrownBy(() -> favoriteManageService.cancel(study.getId(), member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.NOT_FAVORITE_MARKED.getMessage());
        }

        @Test
        @DisplayName("찜 취소에 성공한다")
        void success() {
            // given
            Favorite favorite = favoriteRepository.save(Favorite.favoriteMarking(study.getId(), member.getId()));

            // when
            favoriteManageService.cancel(study.getId(), member.getId());

            // then
            assertThat(favoriteRepository.existsById(favorite.getId())).isFalse();
        }
    }
}
