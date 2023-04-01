package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> StudySimpleQueryRepository 테스트")
class StudySimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Study study1;
    private Study study2;

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());

        study1 = studyRepository.save(SPRING.toOnlineStudy(host));
        study2 = studyRepository.save(JPA.toOnlineStudy(host));
    }
    
    @Test
    @DisplayName("전체 스터디의 ID + Hashtag를 조회한다")
    void findHashtags() {
        // when
        List<BasicHashtag> result = studyRepository.findHashtags();

        // then
        assertThat(result).hasSize(study1.getHashtags().size() + study2.getHashtags().size());
    }

    @Test
    @DisplayName("특정 스터디의 Hashtag 리스트를 조회한다")
    void findHashtagsByStudyId() {
        // when
        List<String> result = studyRepository.findHashtagsByStudyId(study1.getId());

        // then
        assertAll(
                () -> assertThat(result).hasSize(study1.getHashtags().size()),
                () -> assertThat(result).containsAll(study1.getHashtags())
        );
    }
}
