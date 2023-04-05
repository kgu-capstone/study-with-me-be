package com.kgu.studywithme.study.domain.notice.comment;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> CommentRepository 테스트")
class CommentRepositoryTest extends RepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Notice notice;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        Study study = studyRepository.save(SPRING.toOnlineStudy(host));
        notice = noticeRepository.save(Notice.writeNotice(study, "공지사항", "내용"));
    }

    @Test
    @DisplayName("공지사항의 댓글들을 삭제한다")
    void deleteByNoticeId() {
        // given
        notice.addComment(host, "댓글1");
        notice.addComment(host, "댓글2");
        notice.addComment(host, "댓글3");

        // when
        commentRepository.deleteByNoticeId(notice.getId());

        // then
        Notice findNotice = noticeRepository.findById(notice.getId()).orElseThrow();
        assertThat(findNotice.getComments()).hasSize(0);
    }

    @Test
    @DisplayName("댓글 ID + 작성자 ID로 댓글을 조회한다")
    void existsByIdAndWriterId() {
        // given
        Comment comment = commentRepository.save(Comment.writeComment(notice, host, "확인했습니다."));

        // when
        boolean actual1 = commentRepository.existsByIdAndWriterId(comment.getId(), host.getId());
        boolean actual2 = commentRepository.existsByIdAndWriterId(comment.getId() + 100L, host.getId());
        boolean actual3 = commentRepository.existsByIdAndWriterId(comment.getId(), host.getId() + 100L);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isFalse()
        );
    }
}