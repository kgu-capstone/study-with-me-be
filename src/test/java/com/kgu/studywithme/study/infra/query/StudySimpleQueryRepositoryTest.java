package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> StudySimpleQueryRepository 테스트")
class StudySimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private Member host;
    private Member member;
    private final Study[] programming = new Study[7];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        programming[0] = studyRepository.save(SPRING.toOnlineStudy(host));
        programming[1] = studyRepository.save(JPA.toOnlineStudy(host));
        programming[2] = studyRepository.save(REAL_MYSQL.toOfflineStudy(host));
        programming[3] = studyRepository.save(KOTLIN.toOnlineStudy(host));
        programming[4] = studyRepository.save(NETWORK.toOnlineStudy(host));
        programming[5] = studyRepository.save(EFFECTIVE_JAVA.toOnlineStudy(host));
        programming[6] = studyRepository.save(AWS.toOfflineStudy(host));
    }
    
    @Test
    @DisplayName("전체 스터디의 ID + Hashtag를 조회한다")
    void findHashtags() {
        // when
        List<BasicHashtag> result = studyRepository.findHashtags();

        // then
        int totalSize = Arrays.stream(programming)
                .mapToInt(study -> study.getHashtags().size())
                .sum();
        assertThat(result).hasSize(totalSize);
    }
    
    @Test
    @DisplayName("신청한 스터디에 대한 정보를 조회한다")
    void findApplyStudyByMemberId() {
        /* 7개 신청 */
        applyStudy(member, programming[0], programming[1], programming[2], programming[3], programming[4], programming[5], programming[6]);
        List<SimpleStudy> result1 = studyRepository.findApplyStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );

        /* 2개 승인 & 1개 거부 */
        participateStudy(member, programming[6], programming[5]);
        rejectStudy(member, programming[4]);
        List<SimpleStudy> result2 = studyRepository.findApplyStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        /* 2개 승인 & 1개 거부 */
        participateStudy(member, programming[3], programming[2]);
        rejectStudy(member, programming[1]);
        List<SimpleStudy> result3 = studyRepository.findApplyStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result3,
                List.of(programming[0])
        );
    }

    @Test
    @DisplayName("참여하고 있는 스터디에 대한 정보를 조회한다")
    void findParticipateStudyByMemberId() {
        // Case 1
        applyAndParticipateStudy(member, programming[0], programming[1], programming[2], programming[3]);

        List<SimpleStudy> result1 = studyRepository.findParticipateStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        // Case 2
        applyAndParticipateStudy(member, programming[4], programming[5], programming[6]);

        List<SimpleStudy> result2 = studyRepository.findParticipateStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );

        // Case 3 (host)
        List<SimpleStudy> result3 = studyRepository.findParticipateStudyByMemberId(host.getId());
        assertThatStudiesMatch(
                result3,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );
    }

    @Test
    @DisplayName("졸업한 스터디에 대한 정보를 조회한다")
    void findGraduatedStudyByMemberId() {
        // Case 1
        graduateStudy(member, programming[0], programming[1], programming[2], programming[3]);

        List<SimpleStudy> result1 = studyRepository.findGraduatedStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        // Case 2
        graduateStudy(member, programming[4], programming[5], programming[6]);

        List<SimpleStudy> result2 = studyRepository.findGraduatedStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );
    }

    @Test
    @DisplayName("찜 등록한 스터디에 대한 정보를 조회한다")
    void findFavoriteStudyByMemberId() {
        // Case 1
        favoriteStudy(member, programming[0], programming[1], programming[2], programming[3]);

        List<SimpleStudy> result1 = studyRepository.findFavoriteStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        // Case 2
        favoriteStudy(member, programming[4], programming[5], programming[6]);

        List<SimpleStudy> result2 = studyRepository.findFavoriteStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );
    }

    private void applyStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
        }
    }

    private void participateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.approveParticipation(member);
        }
    }

    private void rejectStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.rejectParticipation(member);
        }
    }

    private void applyAndParticipateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    private void graduateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
            study.approveParticipation(member);
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
                    () -> assertThat(actual.getCategory()).isEqualTo(expected.getCategory().getName()),
                    () -> assertThat(actual.getThumbnail()).isEqualTo(expected.getThumbnail().getImageName()),
                    () -> assertThat(actual.getThumbnailBackground()).isEqualTo(expected.getThumbnail().getBackground())
            );
        }
    }
}
