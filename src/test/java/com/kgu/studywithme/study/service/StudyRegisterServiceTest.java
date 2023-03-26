package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.study.controller.utils.StudyRegisterRequestUtils.createOnlineStudyRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyRegisterService 테스트")
class StudyRegisterServiceTest extends ServiceTest {
    @Autowired
    private StudyRegisterService studyRegisterService;

    private Member host;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
    }

    @Nested
    @DisplayName("스터디 생성")
    class register {
        @Test
        @DisplayName("이미 사용하고 있는 스터디 이름이면 생성에 실패한다")
        void duplicateNameOnline() {
            // given
            StudyRegisterRequest request = createOnlineStudyRegisterRequest();
            studyRegisterService.register(request, host.getId());

            // when - then
            assertThatThrownBy(() -> studyRegisterService.register(request, host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());
        }

        @Test
        @DisplayName("스터디 생성에 성공한다")
        void success() {
            // given
            StudyRegisterRequest request = createOnlineStudyRegisterRequest();

            // when
            Long studyId = studyRegisterService.register(request, host.getId());

            // then
            Study findStudy = studyRepository.findByIdWithHost(studyId).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(findStudy.getHost()).isEqualTo(host),
                    () -> assertThat(findStudy.getParticipants()).containsExactly(host)
            );
        }
    }
}
