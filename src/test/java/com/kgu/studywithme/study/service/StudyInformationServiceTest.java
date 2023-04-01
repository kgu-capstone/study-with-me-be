package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.utils.MemberAgeCalculator;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.service.dto.response.ReviewAssembler;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
import com.kgu.studywithme.study.service.dto.response.StudyReview;
import com.kgu.studywithme.study.service.dto.response.StudyReviewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyInformationService 테스트")
class StudyInformationServiceTest extends ServiceTest {
    @Autowired
    private StudyInformationService studyInformationService;

    private Member host;
    private final Member[] members = new Member[5];
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));

        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());

        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    @Test
    @DisplayName("스터디의 기본 정보를 조회한다")
    void getInformation() {
        // when
        StudyInformation information = studyInformationService.getInformation(study.getId());

        // then
        assertAll(
                // Study
                () -> assertThat(information.id()).isEqualTo(study.getId()),
                () -> assertThat(information.name()).isEqualTo(study.getNameValue()),
                () -> assertThat(information.description()).isEqualTo(study.getDescriptionValue()),
                () -> assertThat(information.category()).isEqualTo(study.getCategory().getName()),
                () -> assertThat(information.type()).isEqualTo(study.getType().getDescription()),
                () -> assertThat(information.area()).isNull(),
                () -> assertThat(information.recruitmentStatus()).isEqualTo(study.getRecruitmentStatus().getDescription()),
                () -> assertThat(information.currentMembers()).isEqualTo(study.getApproveParticipants().size()),
                () -> assertThat(information.maxMembers()).isEqualTo(study.getMaxMembers()),
                () -> assertThat(information.averageAge()).isEqualTo(MemberAgeCalculator.getAverage(getBirthList())),
                () -> assertThat(information.hashtags()).containsAll(study.getHashtags()),
                // Host
                () -> assertThat(information.host().id()).isEqualTo(host.getId()),
                () -> assertThat(information.host().nickname()).isEqualTo(host.getNicknameValue()),
                () -> assertThat(information.host().profileUrl()).isEqualTo(host.getProfileUrl())
        );
    }
    
    @Test
    @DisplayName("스터디 졸업자들의 리뷰를 조회한다")
    void getReviews() {
        // given
        graduateAllParticipant();
        
        // when
        ReviewAssembler result = studyInformationService.getReviews(study.getId());

        // then
        assertAll(
                () -> assertThat(result.graduateCount()).isEqualTo(members.length),
                () -> assertThat(result.reviews()).hasSize(members.length),
                () -> {
                    List<String> reviewers = result.reviews()
                            .stream()
                            .map(StudyReview::reviewer)
                            .map(StudyReviewer::nickname)
                            .toList();

                    assertThat(reviewers).containsAll(
                            Arrays.stream(members)
                                    .map(Member::getNicknameValue)
                                    .toList()
                    );
                }
        );
    }

    private List<LocalDate> getBirthList() {
        List<LocalDate> list = new ArrayList<>();
        list.add(host.getBirth());

        for (Member member : members) {
            list.add(member.getBirth());
        }

        return list;
    }

    private void graduateAllParticipant() {
        for (Member member : members) {
            study.graduateParticipant(member);
            study.writeReview(member, "좋은 스터디");
        }
    }
}
