package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Study [Service Layer] -> StudyRegisterService 테스트")
class StudyRegisterServiceTest extends ServiceTest {
    @Autowired
    private StudyRegisterService studyRegisterService;

    @Nested
    @DisplayName("스터디 생성")
    class register {
        @Test
        @DisplayName("이미 사용하고 있는 스터디 이름이면 생성에 실패한다 - 온라인")
        void duplicateNameOnline() {
            // given
            Member member = memberRepository.save(JIWON.toMember());

            Study study = createDuplicateOnlineStudy();
            Study newStudy = createDuplicateOnlineStudy();

            studyRegisterService.register(study, member.getId());

            // when - then
            assertThatThrownBy(() -> studyRegisterService.register(newStudy, member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        }

        @Test
        @DisplayName("이미 사용하고 있는 스터디 이름이면 생성에 실패한다 - 오프라인")
        void duplicateNameOffline() {
            // given
            Member member = memberRepository.save(JIWON.toMember());

            Study study = createDuplicateOfflineStudy();
            Study newStudy = createDuplicateOfflineStudy();

            studyRegisterService.register(study, member.getId());

            // when - then
            assertThatThrownBy(() -> studyRegisterService.register(newStudy, member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        }

        private Study createDuplicateOnlineStudy() {
            return Study.builder()
                    .name(StudyName.from(TOEIC.name()))
                    .description(Description.from(TOEIC.getDescription()))
                    .category(TOEIC.getCategory())
                    .capacity(Capacity.from(TOEIC.getCapacity()))
                    .type(TOEIC.getType())
                    .hashtags(TOEIC.getHashtags())
                    .build();
        }

        private Study createDuplicateOfflineStudy() {
            return Study.builder()
                    .name(StudyName.from(TOSS_INTERVIEW.name()))
                    .description(Description.from(TOSS_INTERVIEW.getDescription()))
                    .category(TOSS_INTERVIEW.getCategory())
                    .area(TOSS_INTERVIEW.getArea())
                    .capacity(Capacity.from(TOSS_INTERVIEW.getCapacity()))
                    .type(TOSS_INTERVIEW.getType())
                    .hashtags(TOSS_INTERVIEW.getHashtags())
                    .build();
        }

    }
}