package com.kgu.studywithme.study.service.notice;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> NoticeCommentService 테스트")
class NoticeCommentServiceTest extends ServiceTest {
    @Autowired
    private NoticeCommentService noticeCommentService;

    private Member host;
    private Member member;
    private Notice notice;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        Study study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member);
        study.approveParticipation(member);

        notice = noticeRepository.save(Notice.writeNotice(study, "title", "content"));
    }

    @Nested
    @DisplayName("공지사항 댓글 등록")
    class register {
        @Test
        @DisplayName("스터디 참여자가 아니면 댓글을 등록할 수 없다")
        void throwExceptionByMemberIsNotParticipant() {
            Member anonymous = memberRepository.save(ANONYMOUS.toMember());

            assertThatThrownBy(() -> noticeCommentService.register(notice.getId(), anonymous.getId(), "댓글"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT.getMessage());
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 등록에 성공한다")
        void success() {
            // given
            noticeCommentService.register(notice.getId(), host.getId(), "댓글");

            // when - then
            Notice findNotice = noticeRepository.findById(notice.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findNotice.getComments()).hasSize(1),
                    () -> assertThat(findNotice.getComments())
                            .map(Comment::getContent)
                            .containsExactly("댓글")
            );
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 삭제")
    class remove {
        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = commentRepository.save(Comment.writeComment(notice, host, "댓글"));
        }

        @Test
        @DisplayName("작성자가 아니라면 댓글을 삭제할 수 없다")
        void throwExceptionByMemberIsNotWriter() {
            assertThatThrownBy(() -> noticeCommentService.remove(comment.getId(), member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 삭제에 성공한다")
        void success() {
            // when
            noticeCommentService.remove(comment.getId(), host.getId());

            // then
            assertThat(commentRepository.existsById(comment.getId())).isFalse();
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 수정")
    class update {
        private Comment comment;

        @BeforeEach
        void setUp() {
            comment = commentRepository.save(Comment.writeComment(notice, host, "댓글"));
        }

        @Test
        @DisplayName("작성자가 아니라면 댓글을 수정할 수 없다")
        void throwExceptionByMemberIsNotWriter() {
            assertThatThrownBy(() -> noticeCommentService.update(comment.getId(), member.getId(), "수정된 댓글"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 수정에 성공한다")
        void success() {
            // when
            noticeCommentService.update(comment.getId(), host.getId(), "수정된 댓글");

            // then
            assertThat(comment.getContent()).isEqualTo("수정된 댓글");
        }
    }
}
