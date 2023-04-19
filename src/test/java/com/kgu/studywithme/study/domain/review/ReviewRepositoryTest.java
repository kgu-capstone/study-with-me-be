package com.kgu.studywithme.study.domain.review;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> ReviewRepository 테스트")
class ReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member host;
    private Member member;
    private Review review;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        Study study = studyRepository.save(SPRING.toOnlineStudy(host));

        study.applyParticipation(member);
        study.approveParticipation(member);
        study.graduateParticipant(member);

        review = reviewRepository.save(Review.writeReview(study, member, "It's review"));
    }

    @Test
    @DisplayName("리뷰 ID + 사용자 ID를 통해서 리뷰 작성자인지 확인한다")
    void existsByIdAndWriterId() {
        // when
        boolean actual1 = reviewRepository.existsByIdAndWriterId(review.getId(), member.getId());
        boolean actual2 = reviewRepository.existsByIdAndWriterId(review.getId(), host.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
