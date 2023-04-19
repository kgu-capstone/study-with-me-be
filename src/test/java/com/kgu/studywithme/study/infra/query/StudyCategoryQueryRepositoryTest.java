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
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
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

    private static final String TOTAL = null;
    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";
    private static final Pageable PAGE_REQUEST_1 = getDefaultPageRequest(0);
    private static final Pageable PAGE_REQUEST_2 = getDefaultPageRequest(1);
    private static final Pageable PAGE_REQUEST_3 = getDefaultPageRequest(2);
    private static final LocalDateTime NOW = LocalDateTime.now();

    private Member host;
    private final Member[] member = new Member[9];
    private final Study[] language = new Study[7];
    private final Study[] interview = new Study[5];
    private final Study[] programming = new Study[12];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());

        member[0] = memberRepository.save(DUMMY1.toMember());
        member[1] = memberRepository.save(DUMMY2.toMember());
        member[2] = memberRepository.save(DUMMY3.toMember());
        member[3] = memberRepository.save(DUMMY4.toMember());
        member[4] = memberRepository.save(DUMMY5.toMember());
        member[5] = memberRepository.save(DUMMY6.toMember());
        member[6] = memberRepository.save(DUMMY7.toMember());
        member[7] = memberRepository.save(DUMMY8.toMember());
        member[8] = memberRepository.save(DUMMY9.toMember());

        language[0] = TOEIC.toOnlineStudy(host);
        language[1] = TOEFL.toOnlineStudy(host);
        language[2] = JAPANESE.toOnlineStudy(host);
        language[3] = CHINESE.toOnlineStudy(host);
        language[4] = FRENCH.toOnlineStudy(host);
        language[5] = GERMAN.toOnlineStudy(host);
        language[6] = ARABIC.toOnlineStudy(host);

        interview[0] = TOSS_INTERVIEW.toOfflineStudy(host);
        interview[1] = KAKAO_INTERVIEW.toOfflineStudy(host);
        interview[2] = NAVER_INTERVIEW.toOfflineStudy(host);
        interview[3] = LINE_INTERVIEW.toOfflineStudy(host);
        interview[4] = GOOGLE_INTERVIEW.toOfflineStudy(host);

        programming[0] = SPRING.toOnlineStudy(host);
        programming[1] = JPA.toOnlineStudy(host);
        programming[2] = REAL_MYSQL.toOfflineStudy(host);
        programming[3] = KOTLIN.toOnlineStudy(host);
        programming[4] = NETWORK.toOnlineStudy(host);
        programming[5] = EFFECTIVE_JAVA.toOnlineStudy(host);
        programming[6] = AWS.toOfflineStudy(host);
        programming[7] = DOCKER.toOnlineStudy(host);
        programming[8] = KUBERNETES.toOnlineStudy(host);
        programming[9] = PYTHON.toOnlineStudy(host);
        programming[10] = RUST.toOnlineStudy(host);
        programming[11] = OS.toOnlineStudy(host);
    }

    @Nested
    @DisplayName("각 카테고리 별 스터디 리스트 조회")
    class findStudyByCategory {
        @Test
        @DisplayName("최신순으로 프로그래밍 스터디 리스트를 조회한다")
        void date() {
            // given
            initDataWithRegisterDate();
            StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, ONLINE, null, null);
            StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, OFFLINE, null, null);
            StudyCategoryCondition totalCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, TOTAL, null, null);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[5], programming[4], programming[3]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(programming[1], programming[0]);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(result2.getContent(), expect2);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result3 = studyRepository.findStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect3 = List.of(programming[6], programming[2]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            Slice<BasicStudy> result4 = studyRepository.findStudyByCategory(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect4 = List.of();
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);

            /* 온라인 + 오프라인 통합 */
            Slice<BasicStudy> result5 = studyRepository.findStudyByCategory(totalCondition, PAGE_REQUEST_1);
            List<Study> expect5 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[6], programming[5], programming[4]);
            assertThat(result5.hasNext()).isTrue();
            assertThatStudiesMatch(result5.getContent(), expect5);

            Slice<BasicStudy> result6 = studyRepository.findStudyByCategory(totalCondition, PAGE_REQUEST_2);
            List<Study> expect6 = List.of(programming[3], programming[2], programming[1], programming[0]);
            assertThat(result6.hasNext()).isFalse();
            assertThatStudiesMatch(result6.getContent(), expect6);
        }

        @Test
        @DisplayName("최신순 + 오프라인 지역으로 면접 스터디 리스트를 조회한다")
        void dateWithRegion() {
            // given
            initDataWithRegisterDate();
            StudyCategoryCondition condition1 = new StudyCategoryCondition(INTERVIEW, SORT_DATE, OFFLINE, "경기도", "성남시");
            StudyCategoryCondition condition2 = new StudyCategoryCondition(INTERVIEW, SORT_DATE, OFFLINE, null, "성남시");
            StudyCategoryCondition condition3 = new StudyCategoryCondition(INTERVIEW, SORT_DATE, OFFLINE, "경기도", null);

            // 서울 특별시 & 강남구
            Slice<BasicStudy> result1 = studyRepository.findStudyByCategory(condition1, PAGE_REQUEST_1);
            Slice<BasicStudy> result2 = studyRepository.findStudyByCategory(condition2, PAGE_REQUEST_1);
            Slice<BasicStudy> result3 = studyRepository.findStudyByCategory(condition3, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isFalse();
            assertThat(result2.hasNext()).isFalse();
            assertThat(result3.hasNext()).isFalse();

            List<Study> expect = List.of(interview[3], interview[2], interview[1]);
            assertThatStudiesMatch(result1.getContent(), expect);
            assertThatStudiesMatch(result2.getContent(), expect);
            assertThatStudiesMatch(result3.getContent(), expect);
        }

        @Test
        @DisplayName("찜이 많은 순으로 프로그래밍 스터디 리스트를 조회한다")
        void favorite() {
            // given
            initDataWithFavorite();
            StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_FAVORITE, ONLINE, null, null);
            StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_FAVORITE, OFFLINE, null, null);
            StudyCategoryCondition totalCondition = new StudyCategoryCondition(PROGRAMMING, SORT_FAVORITE, TOTAL, null, null);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(programming[9], programming[3], programming[5], programming[8], programming[7], programming[0], programming[11], programming[10]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(programming[1], programming[4]);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(result2.getContent(), expect2);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result3 = studyRepository.findStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect3 = List.of(programming[2], programming[6]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            Slice<BasicStudy> result4 = studyRepository.findStudyByCategory(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect4 = List.of();
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);

            /* 온라인 + 오프라인 통합 */
            Slice<BasicStudy> result5 = studyRepository.findStudyByCategory(totalCondition, PAGE_REQUEST_1);
            List<Study> expect5 = List.of(programming[9], programming[3], programming[2], programming[6], programming[5], programming[8], programming[7], programming[0]);
            assertThat(result5.hasNext()).isTrue();
            assertThatStudiesMatch(result5.getContent(), expect5);

            Slice<BasicStudy> result6 = studyRepository.findStudyByCategory(totalCondition, PAGE_REQUEST_2);
            List<Study> expect6 = List.of(programming[11], programming[10], programming[1], programming[4]);
            assertThat(result6.hasNext()).isFalse();
            assertThatStudiesMatch(result6.getContent(), expect6);
        }

        @Test
        @DisplayName("리뷰가 많은 순으로 프로그래밍 스터디 리스트를 조회한다")
        void review() {
            // given
            initDataWithReviews();
            StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_REVIEW, ONLINE, null, null);
            StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_REVIEW, OFFLINE, null, null);
            StudyCategoryCondition totalCondition = new StudyCategoryCondition(PROGRAMMING, SORT_REVIEW, TOTAL, null, null);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(programming[9], programming[3], programming[5], programming[8], programming[7], programming[0], programming[11], programming[10]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(programming[1], programming[4]);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(result2.getContent(), expect2);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result3 = studyRepository.findStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect3 = List.of(programming[2], programming[6]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            Slice<BasicStudy> result4 = studyRepository.findStudyByCategory(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect4 = List.of();
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);

            /* 온라인 + 오프라인 통합 */
            Slice<BasicStudy> result5 = studyRepository.findStudyByCategory(totalCondition, PAGE_REQUEST_1);
            List<Study> expect5 = List.of(programming[9], programming[3], programming[2], programming[6], programming[5], programming[8], programming[7], programming[0]);
            assertThat(result5.hasNext()).isTrue();
            assertThatStudiesMatch(result5.getContent(), expect5);

            Slice<BasicStudy> result6 = studyRepository.findStudyByCategory(totalCondition, PAGE_REQUEST_2);
            List<Study> expect6 = List.of(programming[11], programming[10], programming[1], programming[4]);
            assertThat(result6.hasNext()).isFalse();
            assertThatStudiesMatch(result6.getContent(), expect6);
        }
    }

    @Nested
    @DisplayName("사용자의 관심사에 따른 스터디 리스트 조회 [언어 / 인터뷰 / 프로그래밍]")
    class findStudyByRecommend {
        @Test
        @DisplayName("최신순으로 스터디 리스트를 조회한다")
        void date() {
            // given
            initDataWithRegisterDate();
            StudyRecommendCondition onlineCondition = new StudyRecommendCondition(host.getId(), SORT_DATE, ONLINE, null, null);
            StudyRecommendCondition offlineCondition = new StudyRecommendCondition(host.getId(), SORT_DATE, OFFLINE, null, null);
            StudyRecommendCondition totalCondition = new StudyRecommendCondition(host.getId(), SORT_DATE, TOTAL, null, null);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[5], programming[4], programming[3]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(programming[1], programming[0], language[6], language[5], language[4], language[3], language[2], language[1]);
            assertThat(result2.hasNext()).isTrue();
            assertThatStudiesMatch(result2.getContent(), expect2);

            Slice<BasicStudy> result3 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            List<Study> expect3 = List.of(language[0]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result4 = studyRepository.findStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect4 = List.of(programming[6], programming[2], interview[4], interview[3], interview[2], interview[1], interview[0]);
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);

            Slice<BasicStudy> result5 = studyRepository.findStudyByRecommend(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect5 = List.of();
            assertThat(result5.hasNext()).isFalse();
            assertThatStudiesMatch(result5.getContent(), expect5);

            /* 온라인 + 오프라인 통합 */
            Slice<BasicStudy> result6 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            List<Study> expect6 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[6], programming[5], programming[4]);
            assertThat(result6.hasNext()).isTrue();
            assertThatStudiesMatch(result6.getContent(), expect6);

            Slice<BasicStudy> result7 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            List<Study> expect7 = List.of(programming[3], programming[2], programming[1], programming[0], interview[4], interview[3], interview[2], interview[1]);
            assertThat(result7.hasNext()).isTrue();
            assertThatStudiesMatch(result7.getContent(), expect7);

            Slice<BasicStudy> result8 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            List<Study> expect8 = List.of(interview[0], language[6], language[5], language[4], language[3], language[2], language[1], language[0]);
            assertThat(result8.hasNext()).isFalse();
            assertThatStudiesMatch(result8.getContent(), expect8);
        }

        @Test
        @DisplayName("최신순 + 오프라인 지역으로 스터디 리스트를 조회한다")
        void dateWithRegion() {
            // given
            initDataWithRegisterDate();
            StudyRecommendCondition condition1 = new StudyRecommendCondition(host.getId(), SORT_DATE, OFFLINE, "서울특별시", "강남구");
            StudyRecommendCondition condition2 = new StudyRecommendCondition(host.getId(), SORT_DATE, OFFLINE, null, "강남구");
            StudyRecommendCondition condition3 = new StudyRecommendCondition(host.getId(), SORT_DATE, OFFLINE, "서울특별시", null);

            // 서울 특별시 & 강남구
            Slice<BasicStudy> result1 = studyRepository.findStudyByRecommend(condition1, PAGE_REQUEST_1);
            Slice<BasicStudy> result2 = studyRepository.findStudyByRecommend(condition2, PAGE_REQUEST_1);
            Slice<BasicStudy> result3 = studyRepository.findStudyByRecommend(condition3, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isFalse();
            assertThat(result2.hasNext()).isFalse();
            assertThat(result3.hasNext()).isFalse();

            List<Study> expect = List.of(programming[6], programming[2], interview[4], interview[0]);
            assertThatStudiesMatch(result1.getContent(), expect);
            assertThatStudiesMatch(result2.getContent(), expect);
            assertThatStudiesMatch(result3.getContent(), expect);
        }

        @Test
        @DisplayName("찜이 많은 순으로 스터디 리스트를 조회한다")
        void favorite() {
            // given
            initDataWithFavorite();
            StudyRecommendCondition onlineCondition = new StudyRecommendCondition(host.getId(), SORT_FAVORITE, ONLINE, null, null);
            StudyRecommendCondition offlineCondition = new StudyRecommendCondition(host.getId(), SORT_FAVORITE, OFFLINE, null, null);
            StudyRecommendCondition totalCondition = new StudyRecommendCondition(host.getId(), SORT_FAVORITE, TOTAL, null, null);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            List<Study> expect1 = List.of(programming[9], programming[3], programming[5], language[0], programming[8], programming[7], programming[0], language[5]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(programming[11], programming[10], language[3], programming[1], language[6], language[2], language[1], programming[4]);
            assertThat(result2.hasNext()).isTrue();
            assertThatStudiesMatch(result2.getContent(), expect2);

            Slice<BasicStudy> result3 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            List<Study> expect3 = List.of(language[4]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result4 = studyRepository.findStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect4 = List.of(programming[2], programming[6], interview[3], interview[4], interview[1], interview[2], interview[0]);
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);

            Slice<BasicStudy> result5 = studyRepository.findStudyByRecommend(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect5 = List.of();
            assertThat(result5.hasNext()).isFalse();
            assertThatStudiesMatch(result5.getContent(), expect5);

            /* 온라인 + 오프라인 통합 */
            Slice<BasicStudy> result6 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            List<Study> expect6 = List.of(programming[9], programming[3], programming[2], programming[6], programming[5], interview[3], language[0], programming[8]);
            assertThat(result6.hasNext()).isTrue();
            assertThatStudiesMatch(result6.getContent(), expect6);

            Slice<BasicStudy> result7 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            List<Study> expect7 = List.of(programming[7], programming[0], language[5], programming[11], programming[10], interview[4], interview[1], language[3]);
            assertThat(result7.hasNext()).isTrue();
            assertThatStudiesMatch(result7.getContent(), expect7);

            Slice<BasicStudy> result8 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            List<Study> expect8 = List.of(programming[1], language[6], language[2], language[1], interview[2], programming[4], interview[0], language[4]);
            assertThat(result8.hasNext()).isFalse();
            assertThatStudiesMatch(result8.getContent(), expect8);
        }

        @Test
        @DisplayName("리뷰가 많은 순으로 스터디 리스트를 조회한다")
        void review() {
            // given
            initDataWithReviews();
            StudyRecommendCondition onlineCondition = new StudyRecommendCondition(host.getId(), SORT_REVIEW, ONLINE, null, null);
            StudyRecommendCondition offlineCondition = new StudyRecommendCondition(host.getId(), SORT_REVIEW, OFFLINE, null, null);
            StudyRecommendCondition totalCondition = new StudyRecommendCondition(host.getId(), SORT_REVIEW, TOTAL, null, null);

            /* 온라인 스터디 */
            Slice<BasicStudy> result1 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            System.out.println("result -> " + result1.getContent().stream().map(BasicStudy::getReviewCount).toList());
            List<Study> expect1 = List.of(programming[9], programming[3], programming[5], language[0], programming[8], programming[7], programming[0], language[5]);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(result1.getContent(), expect1);

            Slice<BasicStudy> result2 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            List<Study> expect2 = List.of(programming[11], programming[10], language[3], programming[1], language[6], language[2], language[1], programming[4]);
            assertThat(result2.hasNext()).isTrue();
            assertThatStudiesMatch(result2.getContent(), expect2);

            Slice<BasicStudy> result3 = studyRepository.findStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            List<Study> expect3 = List.of(language[4]);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(result3.getContent(), expect3);

            /* 오프라인 스터디 */
            Slice<BasicStudy> result4 = studyRepository.findStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            List<Study> expect4 = List.of(programming[2], programming[6], interview[3], interview[4], interview[1], interview[2], interview[0]);
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(result4.getContent(), expect4);

            Slice<BasicStudy> result5 = studyRepository.findStudyByRecommend(offlineCondition, PAGE_REQUEST_2);
            List<Study> expect5 = List.of();
            assertThat(result5.hasNext()).isFalse();
            assertThatStudiesMatch(result5.getContent(), expect5);

            /* 온라인 + 오프라인 통합 */
            Slice<BasicStudy> result6 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            List<Study> expect6 = List.of(programming[9], programming[3], programming[2], programming[6], programming[5], interview[3], language[0], programming[8]);
            assertThat(result6.hasNext()).isTrue();
            assertThatStudiesMatch(result6.getContent(), expect6);

            Slice<BasicStudy> result7 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            List<Study> expect7 = List.of(programming[7], programming[0], language[5], programming[11], programming[10], interview[4], interview[1], language[3]);
            assertThat(result7.hasNext()).isTrue();
            assertThatStudiesMatch(result7.getContent(), expect7);

            Slice<BasicStudy> result8 = studyRepository.findStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            List<Study> expect8 = List.of(programming[1], language[6], language[2], language[1], interview[2], programming[4], interview[0], language[4]);
            assertThat(result8.hasNext()).isFalse();
            assertThatStudiesMatch(result8.getContent(), expect8);
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
                    () -> assertThat(actual.getCategory()).isEqualTo(expect.getCategory().getName()),
                    () -> assertThat(actual.getCurrentMembers()).isEqualTo(expect.getApproveParticipants().size()),
                    () -> assertThat(actual.getMaxMembers()).isEqualTo(expect.getMaxMembers()),
                    () -> assertThat(actual.getFavoriteCount()).isEqualTo(expect.getApproveParticipants().size() - 1), // 팀장 제외 스터디 참여자들은 전부 찜
                    () -> assertThat(actual.getReviewCount()).isEqualTo(expect.getReviews().size())
            );
        }
    }

    private void initDataWithRegisterDate() {
        List<Study> buffer = new LinkedList<>();
        int day = language.length + interview.length + programming.length;

        for (Study study : language) {
            ReflectionTestUtils.setField(study, "createdAt", NOW.minusDays(day--));
            buffer.add(study);
        }

        for (Study study : interview) {
            ReflectionTestUtils.setField(study, "createdAt", NOW.minusDays(day--));
            buffer.add(study);
        }

        for (Study study : programming) {
            ReflectionTestUtils.setField(study, "createdAt", NOW.minusDays(day--));
            buffer.add(study);
        }

        studyRepository.saveAll(buffer);
    }

    private void initDataWithFavorite() {
        initDataWithRegisterDate();
        registerWithFavorite(language[0], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        registerWithFavorite(language[1], member[0], member[1], member[2]);
        registerWithFavorite(language[2], member[0], member[1], member[2]);
        registerWithFavorite(language[3], member[0], member[1], member[2], member[3]);
        registerWithFavorite(language[4], member[0]);
        registerWithFavorite(language[5], member[0], member[1], member[2], member[3], member[4]);
        registerWithFavorite(language[6], member[0], member[1], member[2]);

        registerWithFavorite(interview[0], member[0]); // Offline
        registerWithFavorite(interview[1], member[0], member[1], member[2], member[3]); // Offline
        registerWithFavorite(interview[2], member[0], member[1]); // Offline
        registerWithFavorite(interview[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        registerWithFavorite(interview[4], member[0], member[1], member[2], member[3]); // Offline

        registerWithFavorite(programming[0], member[0], member[1], member[2], member[3], member[4]);
        registerWithFavorite(programming[1], member[0], member[1], member[2]);
        registerWithFavorite(programming[2], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]); // Offline
        registerWithFavorite(programming[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]);
        registerWithFavorite(programming[4], member[0]);
        registerWithFavorite(programming[5], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        registerWithFavorite(programming[6], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        registerWithFavorite(programming[7], member[0], member[1], member[2], member[3], member[4]);
        registerWithFavorite(programming[8], member[0], member[1], member[2], member[3], member[4], member[5]);
        registerWithFavorite(programming[9], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7], member[8]);
        registerWithFavorite(programming[10], member[0], member[1], member[2], member[3]);
        registerWithFavorite(programming[11], member[0], member[1], member[2], member[3]);
    }

    private void initDataWithReviews() {
        initDataWithRegisterDate();
        registerWithReview(language[0], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        registerWithReview(language[1], member[0], member[1], member[2]);
        registerWithReview(language[2], member[0], member[1], member[2]);
        registerWithReview(language[3], member[0], member[1], member[2], member[3]);
        registerWithReview(language[4], member[0]);
        registerWithReview(language[5], member[0], member[1], member[2], member[3], member[4]);
        registerWithReview(language[6], member[0], member[1], member[2]);
        registerWithReview(interview[0], member[0]); // Offline
        registerWithReview(interview[1], member[0], member[1], member[2], member[3]); // Offline
        registerWithReview(interview[2], member[0], member[1]); // Offline
        registerWithReview(interview[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        registerWithReview(interview[4], member[0], member[1], member[2], member[3]); // Offline
        registerWithReview(programming[0], member[0], member[1], member[2], member[3], member[4]);
        registerWithReview(programming[1], member[0], member[1], member[2]);
        registerWithReview(programming[2], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]); // Offline
        registerWithReview(programming[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]);
        registerWithReview(programming[4], member[0]);
        registerWithReview(programming[5], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        registerWithReview(programming[6], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        registerWithReview(programming[7], member[0], member[1], member[2], member[3], member[4]);
        registerWithReview(programming[8], member[0], member[1], member[2], member[3], member[4], member[5]);
        registerWithReview(programming[9], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7], member[8]);
        registerWithReview(programming[10], member[0], member[1], member[2], member[3]);
        registerWithReview(programming[11], member[0], member[1], member[2], member[3]);
    }

    private void registerWithFavorite(Study study, Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
            favoriteRepository.save(Favorite.favoriteMarking(study.getId(), member.getId()));
        }
    }

    private void registerWithReview(Study study, Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
            study.graduateParticipant(member);
            study.writeReview(member, "리뷰");
        }
    }
}
