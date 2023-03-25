package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study [Service Layer] -> StudyValidator 테스트")
class StudyValidatorTest extends ServiceTest {
    @Autowired
    private StudyValidator studyValidator;

    private Study study;
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(TOEIC.toOnlineStudy(member));
    }

    @Test
    @DisplayName("스터디 이름 중복에 대한 검증을 진행한다")
    void validateName() {
        final StudyName same = study.getName();
        final StudyName diff = StudyName.from("diff" + same.getValue());

        assertThatThrownBy(() -> studyValidator.validateName(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());
        assertDoesNotThrow(() -> studyValidator.validateName(diff));

    }
}