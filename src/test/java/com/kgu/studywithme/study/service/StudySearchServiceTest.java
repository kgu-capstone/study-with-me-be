package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyType;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_DATE;
import static com.kgu.studywithme.study.utils.PagingConstants.getDefaultPageRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudySearchService 테스트")
class StudySearchServiceTest extends ServiceTest {
    @Autowired
    private StudySearchService studySearchService;

    private Member host;
    private final Study[] language = new Study[7];
    private final Study[] interview = new Study[5];
    private final Study[] programming = new Study[12];

    private static final String TOTAL = null;
    private static final String ONLINE = StudyType.ONLINE.getBrief();
    private static final String OFFLINE = StudyType.OFFLINE.getBrief();
    private static final Pageable PAGE_REQUEST_1 = getDefaultPageRequest(0);
    private static final Pageable PAGE_REQUEST_2 = getDefaultPageRequest(1);
    private static final Pageable PAGE_REQUEST_3 = getDefaultPageRequest(2);
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());

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
    
    @Test
    @DisplayName("특정 카테고리에 해당하는 스터디 리스트를 조회한다 [프로그래밍 카테고리 & 최신순 정렬]")
    void findStudyByCategory() {
        // given
        initDataWithRegisterDate();
        StudyCategoryCondition onlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, ONLINE, null, null);
        StudyCategoryCondition offlineCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, OFFLINE, null, null);
        StudyCategoryCondition totalCondition = new StudyCategoryCondition(PROGRAMMING, SORT_DATE, TOTAL, null, null);

        /* 온라인 스터디 */
        DefaultStudyResponse result1 = studySearchService.findStudyByCategory(onlineCondition, PAGE_REQUEST_1);
        List<Study> expect1 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[5], programming[4], programming[3]);
        assertThat(result1.hasNext()).isTrue();
        assertThatStudiesMatch(result1, expect1);

        DefaultStudyResponse result2 = studySearchService.findStudyByCategory(onlineCondition, PAGE_REQUEST_2);
        List<Study> expect2 = List.of(programming[1], programming[0]);
        assertThat(result2.hasNext()).isFalse();
        assertThatStudiesMatch(result2, expect2);

        /* 오프라인 스터디 */
        DefaultStudyResponse result3 = studySearchService.findStudyByCategory(offlineCondition, PAGE_REQUEST_1);
        List<Study> expect3 = List.of(programming[6], programming[2]);
        assertThat(result3.hasNext()).isFalse();
        assertThatStudiesMatch(result3, expect3);

        DefaultStudyResponse result4 = studySearchService.findStudyByCategory(offlineCondition, PAGE_REQUEST_2);
        List<Study> expect4 = List.of();
        assertThat(result4.hasNext()).isFalse();
        assertThatStudiesMatch(result4, expect4);

        /* 온라인 + 오프라인 통합 */
        DefaultStudyResponse result5 = studySearchService.findStudyByCategory(totalCondition, PAGE_REQUEST_1);
        List<Study> expect5 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[6], programming[5], programming[4]);
        assertThat(result5.hasNext()).isTrue();
        assertThatStudiesMatch(result5, expect5);

        DefaultStudyResponse result6 = studySearchService.findStudyByCategory(totalCondition, PAGE_REQUEST_2);
        List<Study> expect6 = List.of(programming[3], programming[2], programming[1], programming[0]);
        assertThat(result6.hasNext()).isFalse();
        assertThatStudiesMatch(result6, expect6);
    }

    @Test
    @DisplayName("사용자의 관심사에 해당하는 스터디 리스트를 조회한다 [언어 / 인터뷰 / 프로그래밍]")
    void findStudyByRecommend() {
        // given
        initDataWithRegisterDate();
        StudyRecommendCondition onlineCondition = new StudyRecommendCondition(host.getId(), SORT_DATE, ONLINE, null, null);
        StudyRecommendCondition offlineCondition = new StudyRecommendCondition(host.getId(), SORT_DATE, OFFLINE, null, null);
        StudyRecommendCondition totalCondition = new StudyRecommendCondition(host.getId(), SORT_DATE, TOTAL, null, null);

        /* 온라인 스터디 */
        DefaultStudyResponse result1 = studySearchService.findStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
        List<Study> expect1 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[5], programming[4], programming[3]);
        assertThat(result1.hasNext()).isTrue();
        assertThatStudiesMatch(result1, expect1);

        DefaultStudyResponse result2 = studySearchService.findStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
        List<Study> expect2 = List.of(programming[1], programming[0], language[6], language[5], language[4], language[3], language[2], language[1]);
        assertThat(result2.hasNext()).isTrue();
        assertThatStudiesMatch(result2, expect2);

        DefaultStudyResponse result3 = studySearchService.findStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
        List<Study> expect3 = List.of(language[0]);
        assertThat(result3.hasNext()).isFalse();
        assertThatStudiesMatch(result3, expect3);

        /* 오프라인 스터디 */
        DefaultStudyResponse result4 = studySearchService.findStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
        List<Study> expect4 = List.of(programming[6], programming[2], interview[4], interview[3], interview[2], interview[1], interview[0]);
        assertThat(result4.hasNext()).isFalse();
        assertThatStudiesMatch(result4, expect4);

        DefaultStudyResponse result5 = studySearchService.findStudyByRecommend(offlineCondition, PAGE_REQUEST_2);
        List<Study> expect5 = List.of();
        assertThat(result5.hasNext()).isFalse();
        assertThatStudiesMatch(result5, expect5);

        /* 온라인 + 오프라인 통합 */
        DefaultStudyResponse result6 = studySearchService.findStudyByRecommend(totalCondition, PAGE_REQUEST_1);
        List<Study> expect6 = List.of(programming[11], programming[10], programming[9], programming[8], programming[7], programming[6], programming[5], programming[4]);
        assertThat(result6.hasNext()).isTrue();
        assertThatStudiesMatch(result6, expect6);

        DefaultStudyResponse result7 = studySearchService.findStudyByRecommend(totalCondition, PAGE_REQUEST_2);
        List<Study> expect7 = List.of(programming[3], programming[2], programming[1], programming[0], interview[4], interview[3], interview[2], interview[1]);
        assertThat(result7.hasNext()).isTrue();
        assertThatStudiesMatch(result7, expect7);

        DefaultStudyResponse result8 = studySearchService.findStudyByRecommend(totalCondition, PAGE_REQUEST_3);
        List<Study> expect8 = List.of(interview[0], language[6], language[5], language[4], language[3], language[2], language[1], language[0]);
        assertThat(result8.hasNext()).isFalse();
        assertThatStudiesMatch(result8, expect8);
    }

    private void initDataWithRegisterDate() {
        int day = language.length + interview.length + programming.length;

        for (Study study : language) {
            registerStudy(study, NOW.minusDays(day--));
        }

        for (Study study : interview) {
            registerStudy(study, NOW.minusDays(day--));
        }

        for (Study study : programming) {
            registerStudy(study, NOW.minusDays(day--));
        }
    }

    private void registerStudy(Study study, LocalDateTime time) {
        ReflectionTestUtils.setField(study, "createdAt", time);
        studyRepository.save(study);
        favoriteRepository.save(Favorite.favoriteMarking(study.getId(), host.getId()));
    }

    private void assertThatStudiesMatch(DefaultStudyResponse response, List<Study> studies) {
        int expectSize = studies.size();
        assertThat(response.studyList()).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            BasicStudy actual = response.studyList().get(i);
            Study expect = studies.get(i);

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expect.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expect.getNameValue()),
                    () -> assertThat(actual.getType()).isEqualTo(expect.getType().getDescription()),
                    () -> assertThat(actual.getCategory()).isEqualTo(expect.getCategory().getName()),
                    () -> assertThat(actual.getCurrentMembers()).isEqualTo(1), // 스터디 팀장만 참여중
                    () -> assertThat(actual.getMaxMembers()).isEqualTo(expect.getMaxMembers()),
                    () -> assertThat(actual.getHashtags()).containsExactlyInAnyOrderElementsOf(expect.getHashtags()),
                    () -> assertThat(actual.getFavoriteMarkingMembers()).containsExactlyInAnyOrder(host.getId())
            );
        }
    }
}
