package com.kgu.studywithme.study.service.notice;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Study [Service Layer] -> NoticeFindService 테스트")
class NoticeFindServiceTest extends ServiceTest {
    @Autowired
    private NoticeFindService noticeFindService;

    private Notice notice;

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());
        Study study = studyRepository.save(SPRING.toOnlineStudy(host));
        notice = noticeRepository.save(Notice.writeNotice(study, "공지사항", "내용"));
    }

    @Test
    @DisplayName("ID(PK)로 공지사항을 조회한다")
    void findByIdWithStudy() {
        // when
        assertThatThrownBy(() -> noticeFindService.findByIdWithStudy(notice.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.NOTICE_NOT_FOUND.getMessage());

        Notice findNotice = noticeFindService.findByIdWithStudy(notice.getId());

        // then
        assertThat(findNotice).isEqualTo(notice);
    }
}
