package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdate;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyUpdateService 테스트")
class StudyUpdateServiceTest extends ServiceTest {
    @Autowired
    private StudyUpdateService studyUpdateService;

    private Member host;
    private Member member;
    private Study onlineStudy;
    private Study offlineStudy;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        onlineStudy = studyRepository.save(TOEIC.toOnlineStudy(host));
        onlineStudy.applyParticipation(member);
        onlineStudy.approveParticipation(member);

        offlineStudy = studyRepository.save(KAKAO_INTERVIEW.toOfflineStudy(host));
        offlineStudy.applyParticipation(member);
        offlineStudy.approveParticipation(member);
    }

    @Nested
    @DisplayName("정보 수정 폼 제공")
    class getUpdateForm {
        @Test
        @DisplayName("스터디 팀장이 아니면 정보 수정 폼을 제공받을 수 없다")
        void memberIsNotHost() {
            assertThatThrownBy(() -> studyUpdateService.getUpdateForm(onlineStudy.getId(), member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_HOST.getMessage());
        }

        @Test
        @DisplayName("정보 수정 폼 제공에 성공한다 - 온라인")
        void successOn() {
            StudyUpdate response = studyUpdateService.getUpdateForm(onlineStudy.getId(), host.getId());

            assertAll(
                    () -> assertThat(response.name()).isEqualTo(onlineStudy.getName().getValue()),
                    () -> assertThat(response.description()).isEqualTo(onlineStudy.getDescriptionValue()),
                    () -> assertThat(response.category()).isEqualTo(onlineStudy.getCategory().getId()),
                    () -> assertThat(response.type()).isEqualTo(onlineStudy.getType().getDescription()),
                    () -> assertThat(response.province()).isEqualTo(null),
                    () -> assertThat(response.city()).isEqualTo(null),
                    () -> assertThat(response.recruitmentStatus()).isEqualTo(onlineStudy.getRecruitmentStatus().getDescription()),
                    () -> assertThat(response.capacity()).isEqualTo(onlineStudy.getCapacity().getValue()),
                    () -> assertThat(response.hashtags()).containsAll(onlineStudy.getHashtags().stream().toList())
            );
        }

        @Test
        @DisplayName("정보 수정 폼 제공에 성공한다 - 오프라인")
        void successOff() {
            StudyUpdate response = studyUpdateService.getUpdateForm(offlineStudy.getId(), host.getId());

            assertAll(
                    () -> assertThat(response.name()).isEqualTo(offlineStudy.getName().getValue()),
                    () -> assertThat(response.description()).isEqualTo(offlineStudy.getDescriptionValue()),
                    () -> assertThat(response.category()).isEqualTo(offlineStudy.getCategory().getId()),
                    () -> assertThat(response.type()).isEqualTo(offlineStudy.getType().getDescription()),
                    () -> assertThat(response.province()).isEqualTo(offlineStudy.getArea().getProvince()),
                    () -> assertThat(response.city()).isEqualTo(offlineStudy.getArea().getCity()),
                    () -> assertThat(response.recruitmentStatus()).isEqualTo(offlineStudy.getRecruitmentStatus().getDescription()),
                    () -> assertThat(response.capacity()).isEqualTo(offlineStudy.getCapacity().getValue()),
                    () -> assertThat(response.hashtags()).containsAll(offlineStudy.getHashtags().stream().toList())
            );
        }
    }

    @Nested
    @DisplayName("정보 수정")
    class update {
        @Test
        @DisplayName("스터디 팀장이 아니면 스터디 정보를 수정할 수 없다")
        void memberIsNotHost() {
            StudyUpdate request = generateRequestOff(5);

            assertThatThrownBy(() -> studyUpdateService.update(offlineStudy.getId(), request, member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_HOST.getMessage());
        }

        @Test
        @DisplayName("최대 수용인원을 현재 스터디 인원보다 적게 설정할 수 없다")
        void capacityLessThanMembers() {
            StudyUpdate request = generateRequestOff(1);

            assertThatThrownBy(() -> studyUpdateService.update(offlineStudy.getId(), request, host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS.getMessage());
        }

        @Test
        @DisplayName("스터디 정보 수정에 성공한다 - 온라인")
        void successOn() {
            // given
            StudyUpdate request = generateRequestOn(5);

            // when
            studyUpdateService.update(onlineStudy.getId(), request, host.getId());

            // then
            assertAll(
                    () -> assertThat(onlineStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(onlineStudy.getDescriptionValue()).isEqualTo(request.description()),
                    () -> assertThat(onlineStudy.getCategory().getId()).isEqualTo(request.category()),
                    () -> assertThat(onlineStudy.getType().getDescription()).isEqualTo(request.type()),
                    () -> assertThat(onlineStudy.getArea()).isNull(),
                    () -> assertThat(onlineStudy.getRecruitmentStatus().getDescription()).isEqualTo(request.recruitmentStatus()),
                    () -> assertThat(onlineStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(onlineStudy.getHashtags().stream().toList()).containsAll(request.hashtags())
            );
        }

        @Test
        @DisplayName("스터디 정보 수정에 성공한다 - 오프라인")
        void successOff() {
            // given
            StudyUpdate request = generateRequestOff(5);

            // when
            studyUpdateService.update(offlineStudy.getId(), request, host.getId());

            // then
            assertAll(
                    () -> assertThat(offlineStudy.getNameValue()).isEqualTo(request.name()),
                    () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(request.description()),
                    () -> assertThat(offlineStudy.getCategory().getId()).isEqualTo(request.category()),
                    () -> assertThat(offlineStudy.getType().getDescription()).isEqualTo(request.type()),
                    () -> assertThat(offlineStudy.getArea().getProvince()).isEqualTo(request.province()),
                    () -> assertThat(offlineStudy.getArea().getCity()).isEqualTo(request.city()),
                    () -> assertThat(offlineStudy.getRecruitmentStatus().getDescription()).isEqualTo(request.recruitmentStatus()),
                    () -> assertThat(offlineStudy.getCapacity().getValue()).isEqualTo(request.capacity()),
                    () -> assertThat(offlineStudy.getHashtags().stream().toList()).containsAll(request.hashtags())
            );
        }
    }

    public StudyUpdate generateRequestOff(Integer capacity) {
        return StudyUpdate.builder()
                .name(KAKAO_INTERVIEW.name())
                .description(KAKAO_INTERVIEW.getDescription())
                .category(KAKAO_INTERVIEW.getCategory().getId())
                .capacity(capacity)
                .type(KAKAO_INTERVIEW.getType().getDescription())
                .province(KAKAO_INTERVIEW.getArea().getProvince())
                .city(KAKAO_INTERVIEW.getArea().getCity())
                .recruitmentStatus("모집 완료")
                .hashtags(KAKAO_INTERVIEW.getHashtags())
                .build();
    }

    public StudyUpdate generateRequestOn(Integer capacity) {
        return StudyUpdate.builder()
                .name(JAPANESE.name())
                .description(JAPANESE.getDescription())
                .category(JAPANESE.getCategory().getId())
                .capacity(capacity)
                .type(JAPANESE.getType().getDescription())
                .province(null)
                .city(null)
                .recruitmentStatus("모집 완료")
                .hashtags(JAPANESE.getHashtags())
                .build();
    }
}
