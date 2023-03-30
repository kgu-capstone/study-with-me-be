package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infra.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.service.dto.response.DefaultStudyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.study.utils.PagingConstants.SLICE_PER_PAGE;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudySearchService 테스트")
class StudySearchServiceTest extends ServiceTest {
    @Autowired
    private StudySearchService studySearchService;

    private Member host;
    private final Study[] study = new Study[12];
    private static final Pageable PAGE_REQUEST_1 = PageRequest.of(0, SLICE_PER_PAGE);
    private static final Pageable PAGE_REQUEST_2 = PageRequest.of(1, SLICE_PER_PAGE);

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());

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
    
    @Test
    @DisplayName("특정 카테고리에 해당하는 스터디 리스트를 조회한다 [프로그래밍 카테고리 & 최신순 정렬]")
    void findStudyByCategory() {
        // given
        registerStudy(study[0], LocalDateTime.now().minusDays(12));
        registerStudy(study[1], LocalDateTime.now().minusDays(11));
        registerStudy(study[2], LocalDateTime.now().minusDays(10)); // Offline
        registerStudy(study[3], LocalDateTime.now().minusDays(9));
        registerStudy(study[4], LocalDateTime.now().minusDays(8));
        registerStudy(study[5], LocalDateTime.now().minusDays(7));
        registerStudy(study[6], LocalDateTime.now().minusDays(6)); // Offline
        registerStudy(study[7], LocalDateTime.now().minusDays(5));
        registerStudy(study[8], LocalDateTime.now().minusDays(4));
        registerStudy(study[9], LocalDateTime.now().minusDays(3));
        registerStudy(study[10], LocalDateTime.now().minusDays(2));
        registerStudy(study[11], LocalDateTime.now().minusDays(1));

        /* 온라인 스터디 */
        DefaultStudyResponse result1 = studySearchService.findStudyByCategory(PROGRAMMING, SORT_DATE, PAGE_REQUEST_1, true);
        List<Study> expect1 = List.of(study[11], study[10], study[9], study[8], study[7], study[5], study[4], study[3]);
        assertThat(result1.hasNext()).isTrue();
        assertThatStudiesMatch(result1, expect1);

        DefaultStudyResponse result2 = studySearchService.findStudyByCategory(PROGRAMMING, SORT_DATE, PAGE_REQUEST_2, true);
        List<Study> expect2 = List.of(study[1], study[0]);
        assertThat(result2.hasNext()).isFalse();
        assertThatStudiesMatch(result2, expect2);

        /* 오프라인 스터디 */
        DefaultStudyResponse result3 = studySearchService.findStudyByCategory(PROGRAMMING, SORT_DATE, PAGE_REQUEST_1, false);
        List<Study> expect3 = List.of(study[6], study[2]);
        assertThat(result3.hasNext()).isFalse();
        assertThatStudiesMatch(result3, expect3);

        DefaultStudyResponse result4 = studySearchService.findStudyByCategory(PROGRAMMING, SORT_DATE, PAGE_REQUEST_2, false);
        List<Study> expect4 = List.of();
        assertThat(result4.hasNext()).isFalse();
        assertThatStudiesMatch(result4, expect4);
    }

    private void registerStudy(Study study, LocalDateTime time) {
        ReflectionTestUtils.setField(study, "createdAt", time);
        studyRepository.save(study);
        favoriteRepository.save(Favorite.favoriteMarking(study.getId(), host.getId()));
    }

    private void assertThatStudiesMatch(DefaultStudyResponse response, List<Study> studies) {
        int expectSize = studies.size();
        assertThat(response.result()).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            BasicStudy actual = response.result().get(i).study();
            List<String> hashtags = response.result().get(i).hashtags();
            Study expect = studies.get(i);

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expect.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expect.getNameValue()),
                    () -> assertThat(actual.getType()).isEqualTo(expect.getType().getDescription()),
                    () -> assertThat(actual.getCategory()).isEqualTo(PROGRAMMING.getName()),
                    () -> assertThat(actual.getCurrentMembers()).isEqualTo(1), // 스터디 팀장만 참여중
                    () -> assertThat(actual.getMaxMembers()).isEqualTo(expect.getCapacity().getValue()),
                    () -> assertThat(actual.getFavoriteCount()).isEqualTo(1), // 스터디 팀장만 찜
                    () -> assertThat(actual.getReviewCount()).isEqualTo(0),
                    () -> assertThat(hashtags).containsAll(expect.getHashtags())
            );
        }
    }
}
