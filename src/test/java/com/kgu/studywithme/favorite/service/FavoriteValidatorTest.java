package com.kgu.studywithme.favorite.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Favorite [Service Layer] -> FavoriteValidator 테스트")
class FavoriteValidatorTest extends ServiceTest {
    @Autowired
    private FavoriteValidator favoriteValidator;

    private Member host;
    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        study = studyRepository.save(TOEIC.toOnlineStudy(host));
    }

    @Test
    @DisplayName("스터디가 존재하지 않으면 검증에 통과한다")
    void validateNonExist() {
        Favorite favorite = Favorite.favoriteMarking(study.getId(), member.getId());
        favoriteRepository.save(favorite);

        assertThatThrownBy(() -> favoriteValidator.validateNonExist(study.getId(), member.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(FavoriteErrorCode.ALREADY_EXIST.getMessage());
    }

    @Test
    @DisplayName("스터디가 존재하면 검증에 통과한다")
    void validateExist() {
        assertThatThrownBy(() -> favoriteValidator.validateExist(study.getId(), member.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(FavoriteErrorCode.STUDY_IS_NOT_FAVORITE.getMessage());
    }
}