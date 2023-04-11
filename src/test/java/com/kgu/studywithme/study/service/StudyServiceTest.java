package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.study.controller.utils.StudyRegisterRequestUtils.createOfflineStudyRegisterRequest;
import static com.kgu.studywithme.study.controller.utils.StudyRegisterRequestUtils.createOnlineStudyRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyService 테스트")
class StudyServiceTest extends ServiceTest {
    @Autowired
    private StudyService studyService;

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
            studyService.register(request, host.getId());

            // when - then
            assertThatThrownBy(() -> studyService.register(request, host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());
        }

        @Test
        @DisplayName("스터디 생성에 성공한다")
        void success() {
            // given
            StudyRegisterRequest onlineRequest = createOnlineStudyRegisterRequest();
            StudyRegisterRequest offlineRequest = createOfflineStudyRegisterRequest();

            // when
            Long onlineStudyId = studyService.register(onlineRequest, host.getId());
            Long offlineStudyId = studyService.register(offlineRequest, host.getId());

            // then
            Study onlineStudy = studyRepository.findByIdWithHost(onlineStudyId).orElseThrow();
            Study offlineStudy = studyRepository.findByIdWithHost(offlineStudyId).orElseThrow();
            assertAll(
                    () -> assertThat(onlineStudy.getNameValue()).isEqualTo(onlineRequest.name()),
                    () -> assertThat(onlineStudy.getArea()).isNull(),
                    () -> assertThat(offlineStudy.getNameValue()).isEqualTo(offlineRequest.name()),
                    () -> assertThat(offlineStudy.getArea()).isNotNull()
            );
        }
    }

    @Nested
    @DisplayName("정보 수정")
    class update {
        private Study onlineStudy;
        private Study offlineStudy;
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(GHOST.toMember());

            onlineStudy = studyRepository.save(TOEIC.toOnlineStudy(host));
            onlineStudy.applyParticipation(member);
            onlineStudy.approveParticipation(member);

            offlineStudy = studyRepository.save(KAKAO_INTERVIEW.toOfflineStudy(host));
            offlineStudy.applyParticipation(member);
            offlineStudy.approveParticipation(member);
        }

        @Test
        @DisplayName("스터디 팀장이 아니면 스터디 정보를 수정할 수 없다")
        void memberIsNotHost() {
            StudyUpdateRequest request = generateRequestOff(5);

            assertThatThrownBy(() -> studyService.update(offlineStudy.getId(), request, member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_HOST.getMessage());
        }

        @Test
        @DisplayName("최대 수용인원을 현재 스터디 인원보다 적게 설정할 수 없다")
        void capacityLessThanMembers() {
            StudyUpdateRequest request = generateRequestOff(1);

            assertThatThrownBy(() -> studyService.update(offlineStudy.getId(), request, host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS.getMessage());
        }

        @Test
        @DisplayName("스터디 정보 수정에 성공한다 - 온라인")
        void successOn() {
            // given
            StudyUpdateRequest request = generateRequestOn(5);

            // when
            studyService.update(onlineStudy.getId(), request, host.getId());

            // then
            assertAll(
                    () -> assertThat(onlineStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(onlineStudy.getDescriptionValue()).isEqualTo(request.description()),
                    () -> assertThat(onlineStudy.getCategory().getId()).isEqualTo(request.category()),
                    () -> assertThat(onlineStudy.getType().getDescription()).isEqualTo(request.type()),
                    () -> assertThat(onlineStudy.getArea()).isNull(),
                    () -> assertThat(onlineStudy.isRecruitmentComplete()).isEqualTo(request.status()),
                    () -> assertThat(onlineStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(onlineStudy.getHashtags().stream().toList()).containsAll(request.hashtags())
            );
        }

        @Test
        @DisplayName("스터디 정보 수정에 성공한다 - 오프라인")
        void successOff() {
            // given
            StudyUpdateRequest request = generateRequestOff(5);

            // when
            studyService.update(offlineStudy.getId(), request, host.getId());

            // then
            assertAll(
                    () -> assertThat(offlineStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(request.description()),
                    () -> assertThat(offlineStudy.getCategory().getId()).isEqualTo(request.category()),
                    () -> assertThat(offlineStudy.getType().getDescription()).isEqualTo(request.type()),
                    () -> assertThat(offlineStudy.getArea().getProvince()).isEqualTo(request.province()),
                    () -> assertThat(offlineStudy.getArea().getCity()).isEqualTo(request.city()),
                    () -> assertThat(offlineStudy.isRecruitmentComplete()).isEqualTo(request.status()),
                    () -> assertThat(offlineStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(offlineStudy.getHashtags().stream().toList()).containsAll(request.hashtags())
            );
        }
    }

    public StudyUpdateRequest generateRequestOff(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(KAKAO_INTERVIEW.name())
                .description(KAKAO_INTERVIEW.getDescription())
                .category(KAKAO_INTERVIEW.getCategory().getId())
                .capacity(capacity)
                .type(KAKAO_INTERVIEW.getType().getDescription())
                .province(KAKAO_INTERVIEW.getArea().getProvince())
                .city(KAKAO_INTERVIEW.getArea().getCity())
                .status(false)
                .hashtags(KAKAO_INTERVIEW.getHashtags())
                .build();
    }

    public StudyUpdateRequest generateRequestOn(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(JAPANESE.name())
                .description(JAPANESE.getDescription())
                .category(JAPANESE.getCategory().getId())
                .capacity(capacity)
                .type(JAPANESE.getType().getDescription())
                .province(null)
                .city(null)
                .status(false)
                .hashtags(JAPANESE.getHashtags())
                .build();
    }
}
