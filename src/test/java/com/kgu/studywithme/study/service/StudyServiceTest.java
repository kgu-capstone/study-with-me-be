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

import static com.kgu.studywithme.fixture.MemberFixture.*;
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
        void throwExceptionByDuplicateName() {
            // given
            StudyRegisterRequest request = createOnlineStudyRegisterRequest();
            studyService.register(host.getId(), request);

            // when - then
            assertThatThrownBy(() -> studyService.register(host.getId(), request))
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
            Long onlineStudyId = studyService.register(host.getId(), onlineRequest);
            Long offlineStudyId = studyService.register(host.getId(), offlineRequest);

            // then
            Study onlineStudy = studyRepository.findById(onlineStudyId).orElseThrow();
            Study offlineStudy = studyRepository.findById(offlineStudyId).orElseThrow();
            assertAll(
                    () -> assertThat(onlineStudy.getNameValue()).isEqualTo(onlineRequest.name()),
                    () -> assertThat(onlineStudy.getLocation()).isNull(),
                    () -> assertThat(offlineStudy.getNameValue()).isEqualTo(offlineRequest.name()),
                    () -> assertThat(offlineStudy.getLocation()).isNotNull()
            );
        }
    }

    @Nested
    @DisplayName("스터디 정보 수정")
    class update {
        private Study onlineStudy;
        private Study offlineStudy;

        @BeforeEach
        void setUp() {
            Member memberA = memberRepository.save(DUMMY1.toMember());
            Member memberB = memberRepository.save(DUMMY2.toMember());
            Member memberC = memberRepository.save(DUMMY3.toMember());

            onlineStudy = studyRepository.save(TOEIC.toOnlineStudy(host));
            beParticipation(onlineStudy, memberA, memberB, memberC);

            offlineStudy = studyRepository.save(KAKAO_INTERVIEW.toOfflineStudy(host));
            beParticipation(offlineStudy, memberA, memberB, memberC);
        }

        @Test
        @DisplayName("다른 스터디가 사용하고 있는 스터디명으로 수정할 수 없다")
        void throwExceptionByDuplicateName() {
            StudyUpdateRequest request = StudyUpdateRequest.builder()
                    .name(offlineStudy.getNameValue())
                    .build();

            assertThatThrownBy(() -> studyService.update(onlineStudy.getId(), host.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());
        }

        @Test
        @DisplayName("최대 수용인원을 현재 스터디 인원보다 적게 설정할 수 없다")
        void throwExceptionByCapacityCannotBeLessThanParticipants() {
            StudyUpdateRequest request = generateOnlineStudyUpdateRequest(2);

            assertThatThrownBy(() -> studyService.update(onlineStudy.getId(), host.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_PARTICIPANTS.getMessage());
        }

        @Test
        @DisplayName("스터디 정보 수정에 성공한다 - 온라인")
        void successOnlineStudyUpdate() {
            // given
            StudyUpdateRequest request = generateOnlineStudyUpdateRequest(5);

            // when
            studyService.update(onlineStudy.getId(), host.getId(), request);

            // then
            Study findStudy = studyRepository.findById(onlineStudy.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(findStudy.getDescriptionValue()).isEqualTo(request.description()),
                    () -> assertThat(findStudy.getCategory().getId()).isEqualTo(request.category()),
                    () -> assertThat(findStudy.getType().getBrief()).isEqualTo(request.type()),
                    () -> assertThat(findStudy.getLocation()).isNull(),
                    () -> assertThat(findStudy.isRecruitmentComplete()).isFalse(),
                    () -> assertThat(findStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(findStudy.getHashtags()).hasSize(request.hashtags().size()),
                    () -> assertThat(findStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(request.hashtags())
            );
        }

        @Test
        @DisplayName("스터디 정보 수정에 성공한다 - 오프라인")
        void successOfflineStudyUpdate() {
            // given
            StudyUpdateRequest request = generateOfflineStudyUpdateRequest(5);

            // when
            studyService.update(offlineStudy.getId(), host.getId(), request);

            // then
            Study findStudy = studyRepository.findById(offlineStudy.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(findStudy.getDescriptionValue()).isEqualTo(request.description()),
                    () -> assertThat(findStudy.getCategory().getId()).isEqualTo(request.category()),
                    () -> assertThat(findStudy.getType().getBrief()).isEqualTo(request.type()),
                    () -> assertThat(findStudy.getLocation().getProvince()).isEqualTo(request.province()),
                    () -> assertThat(findStudy.getLocation().getCity()).isEqualTo(request.city()),
                    () -> assertThat(findStudy.isRecruitmentComplete()).isFalse(),
                    () -> assertThat(findStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(findStudy.getHashtags()).hasSize(request.hashtags().size()),
                    () -> assertThat(findStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(request.hashtags())
            );
        }
    }

    public StudyUpdateRequest generateOnlineStudyUpdateRequest(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(JAPANESE.name())
                .description(JAPANESE.getDescription())
                .category(JAPANESE.getCategory().getId())
                .capacity(capacity)
                .type(JAPANESE.getType().getBrief())
                .province(null)
                .city(null)
                .recruitmentStatus(true)
                .hashtags(JAPANESE.getHashtags())
                .build();
    }

    public StudyUpdateRequest generateOfflineStudyUpdateRequest(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(GOOGLE_INTERVIEW.name())
                .description(GOOGLE_INTERVIEW.getDescription())
                .category(GOOGLE_INTERVIEW.getCategory().getId())
                .capacity(capacity)
                .type(GOOGLE_INTERVIEW.getType().getBrief())
                .province(GOOGLE_INTERVIEW.getLocation().getProvince())
                .city(GOOGLE_INTERVIEW.getLocation().getCity())
                .recruitmentStatus(true)
                .hashtags(GOOGLE_INTERVIEW.getHashtags())
                .build();
    }

    private void beParticipation(Study study, Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }
}
