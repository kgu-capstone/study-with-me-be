package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.review.Review;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Study [Service Layer] -> StudyReviewService 테스트")
class StudyReviewServiceTest extends ServiceTest {
    @Autowired
    private StudyReviewService studyReviewService;

    private Member member1;
    private Member member2;
    private Study study;

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());
        member1 = memberRepository.save(GHOST.toMember());
        member2 = memberRepository.save(ANONYMOUS.toMember());

        study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member1);
        study.approveParticipation(member1);
        study.graduateParticipant(member1);
    }

    @Nested
    @DisplayName("리뷰 작성")
    class write {
        @Test
        @DisplayName("졸업자가 아니면 리뷰를 작성할 수 없다")
        void memberIsNotGraduate() {
            assertThatThrownBy(() -> studyReviewService.write(study.getId(), member2.getId(), "It's good"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_GRADUATED.getMessage());
        }

        @Test
        @DisplayName("리뷰 작성에 성공한다")
        void success() {
            // given
            studyReviewService.write(study.getId(), member1.getId(), "It's good");

            // when - then
            Study findStudy = studyRepository.findByIdWithReviews(study.getId()).orElseThrow();
            assertThat(findStudy.getReviews().size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("리뷰 삭제")
    class remove {
        private Review review;

        @BeforeEach
        void setUp() {
            review = reviewRepository.save(Review.writeReview(study, member1, "It's good"));
        }

        @Test
        @DisplayName("작성자가 아니면 리뷰를 삭제할 수 없다")
        void memberIsNotWriter() {
            // when - then
            assertThatThrownBy(() -> studyReviewService.remove(review.getId(), member2.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("리뷰 삭제에 성공한다")
        void success() {
            // when
            studyReviewService.remove(review.getId(), member1.getId());

            // then
            assertThat(study.getReviews().size()).isEqualTo(0);
        }
    }
}