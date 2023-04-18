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
                    () -> assertThat(onlineStudy.getArea()).isNull(),
                    () -> assertThat(offlineStudy.getNameValue()).isEqualTo(offlineRequest.name()),
                    () -> assertThat(offlineStudy.getArea()).isNotNull()
            );
        }
    }

    @Nested
    @DisplayName("스터디 정보 수정")
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
        @DisplayName("다른 스터디가 사용하고 있는 스터디명으로 수정할 수 없다")
        void duplicateName() {
            StudyUpdateRequest request = StudyUpdateRequest.builder()
                    .name(offlineStudy.getNameValue())
                    .build();

            assertThatThrownBy(() -> studyService.update(onlineStudy.getId(), member.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());
        }

        @Test
        @DisplayName("스터디 팀장이 아니면 스터디 정보를 수정할 수 없다")
        void memberIsNotHost() {
            StudyUpdateRequest request = generateOnlineStudyUpdateRequest(5);

            assertThatThrownBy(() -> studyService.update(onlineStudy.getId(), member.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_HOST.getMessage());
        }

        @Test
        @DisplayName("최대 수용인원을 현재 스터디 인원보다 적게 설정할 수 없다")
        void capacityLessThanMembers() {
            StudyUpdateRequest request = generateOnlineStudyUpdateRequest(1);

            assertThatThrownBy(() -> studyService.update(onlineStudy.getId(), host.getId(), request))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS.getMessage());
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
                    () -> assertThat(findStudy.getType().getDescription()).isEqualTo(request.type()),
                    () -> assertThat(findStudy.getArea()).isNull(),
                    () -> assertThat(findStudy.isRecruitmentComplete()).isFalse(),
                    () -> assertThat(findStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(findStudy.getHashtags()).hasSize(request.hashtags().size()),
                    () -> assertThat(findStudy.getHashtags()).containsExactlyElementsOf(request.hashtags())
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
                    () -> assertThat(findStudy.getType().getDescription()).isEqualTo(request.type()),
                    () -> assertThat(findStudy.getArea().getProvince()).isEqualTo(request.province()),
                    () -> assertThat(findStudy.getArea().getCity()).isEqualTo(request.city()),
                    () -> assertThat(findStudy.isRecruitmentComplete()).isFalse(),
                    () -> assertThat(findStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(findStudy.getHashtags()).hasSize(request.hashtags().size()),
                    () -> assertThat(findStudy.getHashtags()).containsExactlyElementsOf(request.hashtags())
            );
        }
    }

    public StudyUpdateRequest generateOnlineStudyUpdateRequest(Integer capacity) {
        return StudyUpdateRequest.builder()
                .name(JAPANESE.name())
                .description(JAPANESE.getDescription())
                .category(JAPANESE.getCategory().getId())
                .capacity(capacity)
                .type(JAPANESE.getType().getDescription())
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
                .type(GOOGLE_INTERVIEW.getType().getDescription())
                .province(GOOGLE_INTERVIEW.getArea().getProvince())
                .city(GOOGLE_INTERVIEW.getArea().getCity())
                .recruitmentStatus(true)
                .hashtags(GOOGLE_INTERVIEW.getHashtags())
                .build();
    }
}
