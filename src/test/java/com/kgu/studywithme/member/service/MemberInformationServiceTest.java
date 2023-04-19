package com.kgu.studywithme.member.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
import com.kgu.studywithme.member.service.dto.response.PeerReviewAssembler;
import com.kgu.studywithme.member.service.dto.response.RelatedStudy;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberInformationService 테스트")
class MemberInformationServiceTest extends ServiceTest {
    @Autowired
    private MemberInformationService memberInformationService;

    private Member host;
    private Member member;
    private final Study[] programming = new Study[7];

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());

        host = memberRepository.save(GHOST.toMember());
        programming[0] = studyRepository.save(SPRING.toOnlineStudy(host));
        programming[1] = studyRepository.save(JPA.toOnlineStudy(host));
        programming[2] = studyRepository.save(REAL_MYSQL.toOfflineStudy(host));
        programming[3] = studyRepository.save(KOTLIN.toOnlineStudy(host));
        programming[4] = studyRepository.save(NETWORK.toOnlineStudy(host));
        programming[5] = studyRepository.save(EFFECTIVE_JAVA.toOnlineStudy(host));
        programming[6] = studyRepository.save(AWS.toOfflineStudy(host));
    }

    @Test
    @DisplayName("사용자의 기본 정보를 조회한다")
    void getInformation() {
        // when
        MemberInformation information = memberInformationService.getInformation(member.getId());

        // then
        assertAll(
                () -> assertThat(information.id()).isEqualTo(member.getId()),
                () -> assertThat(information.name()).isEqualTo(member.getName()),
                () -> assertThat(information.nickname()).isEqualTo(member.getNicknameValue()),
                () -> assertThat(information.email()).isEqualTo(member.getEmailValue()),
                () -> assertThat(information.birth()).isEqualTo(member.getBirth()),
                () -> assertThat(information.phone()).isEqualTo(member.getPhone()),
                () -> assertThat(information.gender()).isEqualTo(member.getGender().getValue()),
                () -> assertThat(information.region()).isEqualTo(member.getRegion()),
                () -> assertThat(information.interests()).containsExactlyInAnyOrder(
                        LANGUAGE.getName(), INTERVIEW.getName(), PROGRAMMING.getName()
                )
        );
    }

    @Test
    @DisplayName("사용자가 참여중인 스터디 리스트를 조회한다")
    void getParticipateStudy() {
        // given
        participateStudy(member, programming[0], programming[1], programming[2], programming[3], programming[4], programming[5], programming[6]);
        graduateStudy(member, programming[1], programming[3], programming[6]);

        // when
        RelatedStudy relatedStudy = memberInformationService.getParticipateStudy(member.getId());

        // then
        List<Study> expectParticipate = List.of(programming[5], programming[4], programming[2], programming[0]);
        assertThatStudiesMatch(relatedStudy.result(), expectParticipate);
    }

    @Test
    @DisplayName("사용자가 졸업한 스터디 리스트를 조회한다")
    void getGraduatedStudy() {
        // given
        participateStudy(member, programming[0], programming[1], programming[2], programming[3], programming[4], programming[5], programming[6]);
        graduateStudy(member, programming[1], programming[3], programming[6]);

        // when
        RelatedStudy relatedStudy = memberInformationService.getGraduatedStudy(member.getId());

        // then
        List<Study> expectGraduated = List.of(programming[6], programming[3], programming[1]);
        assertThatStudiesMatch(relatedStudy.result(), expectGraduated);
    }

    @Test
    @DisplayName("사용자가 찜한 스터디 리스트를 조회한다")
    void getFavoriteStudy() {
        // given
        favoriteStudy(member, programming[0], programming[1], programming[3], programming[4], programming[6]);

        // when
        RelatedStudy relatedStudy = memberInformationService.getFavoriteStudy(member.getId());

        // then
        List<Study> expectFavorite = List.of(programming[6], programming[4], programming[3], programming[1], programming[0]);
        assertThatStudiesMatch(relatedStudy.result(), expectFavorite);
    }

    @Test
    @DisplayName("사용자의 피어리뷰를 조회한다")
    void getPeerReviews() {
        // given
        peerReviewRepository.save(PeerReview.doReview(host, member, "host는 최고다."));
        peerReviewRepository.save(PeerReview.doReview(member, host, "member는 최고다."));

        // when
        PeerReviewAssembler hostReview = memberInformationService.getPeerReviews(host.getId());
        PeerReviewAssembler memberReview = memberInformationService.getPeerReviews(member.getId());

        // then
        assertAll(
                () -> assertThat(hostReview.reviews()).hasSize(1),
                () -> assertThat(hostReview.reviews()).containsExactly("host는 최고다."),
                () -> assertThat(memberReview.reviews()).hasSize(1),
                () -> assertThat(memberReview.reviews()).containsExactly("member는 최고다.")
        );
    }

    private void participateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    private void graduateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.graduateParticipant(member);
        }
    }

    private void favoriteStudy(Member member, Study... studies) {
        for (Study study : studies) {
            favoriteRepository.save(Favorite.favoriteMarking(study.getId(), member.getId()));
        }
    }

    private void assertThatStudiesMatch(List<SimpleStudy> result, List<Study> expect) {
        int expectSize = expect.size();
        assertThat(result).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            SimpleStudy actual = result.get(i);
            Study expected = expect.get(i);

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expected.getNameValue()),
                    () -> assertThat(actual.getCategory()).isEqualTo(expected.getCategory().getName())
            );
        }
    }
}
