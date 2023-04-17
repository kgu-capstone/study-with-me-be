package com.kgu.studywithme.study.domain.notice;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> NoticeRepository 테스트")
class NoticeRepositoryTest extends RepositoryTest {
    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Study study;
    private Notice notice;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));
        notice = noticeRepository.save(Notice.writeNotice(study, "공지사항", "내용"));
    }

    @Test
    @DisplayName("공지사항 ID로 공지사항과 스터디가 함께 조회되는지 확인한다")
    void findByIdWithStudy() {
        // when
        Notice findNotice = noticeRepository.findByIdWithStudy(notice.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findNotice.getId()).isEqualTo(notice.getId()),
                () -> assertThat(findNotice.getStudy()).isEqualTo(study),
                () -> assertThat(findNotice.getTitle()).isEqualTo("공지사항"),
                () -> assertThat(findNotice.getContent()).isEqualTo("내용")
        );
    }

    @Test
    @DisplayName("공지사항 ID & 작성자 ID에 해당하는 공지사항이 존재하는지 확인한다")
    void existsByIdAndWriterId() {
        // when
        boolean actual1 = noticeRepository.existsByIdAndWriterId(notice.getId(), host.getId());
        boolean actual2 = noticeRepository.existsByIdAndWriterId(notice.getId(), host.getId() + 100L);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
