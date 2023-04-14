package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;

@DisplayName("Member [Repository Layer] -> PeerReviewRepository 테스트")
public class PeerReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private PeerReviewRepository peerReviewRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사용자의 PeerReview 리스트를 조회한다")
    void findContentByRevieweeId() {
        // given
        Member reviewee = memberRepository.save(JIWON.toMember());
        Member reviewer = memberRepository.save(GHOST.toMember());
        peerReviewRepository.save(PeerReview.doReview(reviewee, reviewer, "BEST!"));

        // when
        List<String> findContent = peerReviewRepository.findContentByRevieweeId(reviewee.getId());

        // then
        Assertions.assertThat(findContent).contains("BEST!");
    }
}
