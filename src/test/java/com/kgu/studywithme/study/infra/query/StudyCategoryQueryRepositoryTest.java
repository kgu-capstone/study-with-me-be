package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.study.utils.PagingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> StudyCategoryQueryRepository 테스트")
class StudyCategoryQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private static final Pageable PAGE_REQUEST_1 = PageRequest.of(0, SLICE_PER_PAGE);
    private static final Pageable PAGE_REQUEST_2 = PageRequest.of(1, SLICE_PER_PAGE);

    private final Member[] graduate = new Member[9];
    private final Study[] study = new Study[12];
    private final DummyDataBuilder dummyDataBuilder = new DummyDataBuilder();

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());

        graduate[0] = memberRepository.save(DUMMY1.toMember());
        graduate[1] = memberRepository.save(DUMMY2.toMember());
        graduate[2] = memberRepository.save(DUMMY3.toMember());
        graduate[3] = memberRepository.save(DUMMY4.toMember());
        graduate[4] = memberRepository.save(DUMMY5.toMember());
        graduate[5] = memberRepository.save(DUMMY6.toMember());
        graduate[6] = memberRepository.save(DUMMY7.toMember());
        graduate[7] = memberRepository.save(DUMMY8.toMember());
        graduate[8] = memberRepository.save(DUMMY9.toMember());

        study[0] = SPRING.toOnlineStudy(host);
        study[1] = JPA.toOnlineStudy(host);
        study[2] = REAL_MYSQL.toOfflineStudy(host);
        study[3] = KOTLIN.toOnlineStudy(host);
        study[4] = NETWORK.toOnlineStudy(host);
        study[5] = EFFECTIVE_JAVA.toOnlineStudy(host);
        study[6] = AWS.toOfflineStudy(host);
        study[7] = DOCKER.toOnlineStudy(host);
        study[8] = KUBERNETES.toOnlineStudy(host);
        study[9] = PYTHON.toOnlineStudy(host);
        study[10] = RUST.toOnlineStudy(host);
        study[11] = OS.toOnlineStudy(host);
    }

    @Nested
    @DisplayName("각 카테고리 별 스터디 리스트 조회 [프로그래밍 카테고리]")
    class findStudyWithCondition {
        @Test
        @DisplayName("최신순으로 스터디 리스트를 조회한다")
        void date() {
            // given
            dummyDataBuilder
                    .registerDate(study[0], LocalDateTime.now().minusDays(12))
                    .registerDate(study[1], LocalDateTime.now().minusDays(11))
                    .registerDate(study[2], LocalDateTime.now().minusDays(10)) // Offline
                    .registerDate(study[3], LocalDateTime.now().minusDays(9))
                    .registerDate(study[4], LocalDateTime.now().minusDays(8))
                    .registerDate(study[5], LocalDateTime.now().minusDays(7))
                    .registerDate(study[6], LocalDateTime.now().minusDays(6)) // Offline
                    .registerDate(study[7], LocalDateTime.now().minusDays(5))
                    .registerDate(study[8], LocalDateTime.now().minusDays(4))
                    .registerDate(study[9], LocalDateTime.now().minusDays(3))
                    .registerDate(study[10], LocalDateTime.now().minusDays(2))
                    .registerDate(study[11], LocalDateTime.now().minusDays(1))
                    .complete();

            StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, true);
            StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, false);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyWithCondition(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(study[11], study[10], study[9], study[8], study[7], study[5], study[4], study[3]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyWithCondition(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(study[1], study[0]);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(result2.getContent(), expect2);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result3 = studyRepository.findStudyWithCondition(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect3 = List.of(study[6], study[2]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            Slice<BasicStudy> result4 = studyRepository.findStudyWithCondition(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect4 = List.of();
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);
        }

        @Test
        @DisplayName("찜이 많은 순으로 스터디 리스트를 조회한다")
        void favorite() {
            // given
            dummyDataBuilder
                    .favorite(study[0], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4])
                    .favorite(study[1], graduate[0], graduate[1], graduate[2])
                    .favorite(study[2], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6], graduate[7]) // Offline
                    .favorite(study[3], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6], graduate[7])
                    .favorite(study[4], graduate[0])
                    .favorite(study[5], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6])
                    .favorite(study[6], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6]) // Offline
                    .favorite(study[7], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4])
                    .favorite(study[8], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5])
                    .favorite(study[9], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6], graduate[7], graduate[8])
                    .favorite(study[10], graduate[0], graduate[1], graduate[2], graduate[3])
                    .favorite(study[11], graduate[0], graduate[1], graduate[2], graduate[3])
                    .complete();

            StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_FAVORITE, true);
            StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_FAVORITE, false);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyWithCondition(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(study[9], study[3], study[5], study[8], study[7], study[0], study[11], study[10]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyWithCondition(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(study[1], study[4]);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(result2.getContent(), expect2);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result3 = studyRepository.findStudyWithCondition(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect3 = List.of(study[2], study[6]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            Slice<BasicStudy> result4 = studyRepository.findStudyWithCondition(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect4 = List.of();
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);
        }

        @Test
        @DisplayName("리뷰가 많은 순으로 스터디 리스트를 조회한다")
        void review() {
            // given
            dummyDataBuilder
                    .review(study[0], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4])
                    .review(study[1], graduate[0], graduate[1], graduate[2])
                    .review(study[2], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6], graduate[7]) // Offline
                    .review(study[3], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6], graduate[7])
                    .review(study[4], graduate[0])
                    .review(study[5], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6])
                    .review(study[6], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6]) // Offline
                    .review(study[7], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4])
                    .review(study[8], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5])
                    .review(study[9], graduate[0], graduate[1], graduate[2], graduate[3], graduate[4], graduate[5], graduate[6], graduate[7], graduate[8])
                    .review(study[10], graduate[0], graduate[1], graduate[2], graduate[3])
                    .review(study[11], graduate[0], graduate[1], graduate[2], graduate[3])
                    .complete();

            StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_REVIEW, true);
            StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_REVIEW, false);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyWithCondition(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(study[9], study[3], study[5], study[8], study[7], study[0], study[11], study[10]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyWithCondition(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(study[1], study[4]);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(result2.getContent(), expect2);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result3 = studyRepository.findStudyWithCondition(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect3 = List.of(study[2], study[6]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            Slice<BasicStudy> result4 = studyRepository.findStudyWithCondition(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect4 = List.of();
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);
        }
    }

    private void assertThatStudiesMatch(List<BasicStudy> actuals, List<Study> studies) {
        int expectSize = studies.size();
        assertThat(actuals).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            BasicStudy actual = actuals.get(i);
            Study expect = studies.get(i);

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expect.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expect.getNameValue()),
                    () -> assertThat(actual.getType()).isEqualTo(expect.getType().getDescription()),
                    () -> assertThat(actual.getCategory()).isEqualTo(PROGRAMMING.getName()),
                    () -> assertThat(actual.getCurrentMembers()).isEqualTo(expect.getApproveParticipants().size()),
                    () -> assertThat(actual.getMaxMembers()).isEqualTo(expect.getCapacity().getValue()),
                    () -> assertThat(actual.getFavoriteCount()).isEqualTo(expect.getApproveParticipants().size() - 1), // 스터디 참여자들은 전부 찜을 했다고 가정
                    () -> assertThat(actual.getReviewCount()).isEqualTo(expect.getReviews().getCount())
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private class DummyDataBuilder {
        public DummyDataBuilder registerDate(Study study, LocalDateTime time) {
            ReflectionTestUtils.setField(study, "createdAt", time);
            studyRepository.save(study);
            return new DummyDataBuilder();
        }

        public DummyDataBuilder favorite(Study study, Member... members) {
            studyRepository.save(study);
            for (Member member : members) {
                study.applyParticipation(member);
                study.approveParticipation(member);
                favoriteRepository.save(Favorite.favoriteMarking(study.getId(), member.getId()));
            }
            return new DummyDataBuilder();
        }

        public DummyDataBuilder review(Study study, Member... members) {
            for (Member member : members) {
                study.applyParticipation(member);
                study.approveParticipation(member);
                study.graduateParticipant(member);
                study.writeReview(member, "리뷰");
            }
            studyRepository.save(study);
            return new DummyDataBuilder();
        }

        public void complete() {}
    }
}
