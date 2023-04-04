package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.controller.dto.request.NoticeCommentRequest;
import com.kgu.studywithme.study.controller.utils.NoticeCommentRequestUtils;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
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
    private NoticeCommentRequest REQUEST = NoticeCommentRequestUtils.createNoticeCommentRequest();

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        Study study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member);
        study.approveParticipation(member);

        notice = noticeRepository.save(Notice.writeNotice(study, "공지사항 제목", "내용"));
    }

    @Nested
    @DisplayName("공지사항 댓글 등록")
    class register {
        @Test
        @DisplayName("공지사항에 대한 댓글 등록에 성공한다")
        void success() {
            // given
            Long savedCommentId = noticeCommentService.register(notice.getId(), member.getId(), REQUEST);

            // when - then
            Comment comment = commentRepository.findById(savedCommentId).orElseThrow();
            assertAll(
                    () -> assertThat(comment.getWriter()).isEqualTo(member),
                    () -> assertThat(comment.getNotice()).isEqualTo(notice),
                    () -> assertThat(comment.getContent()).isEqualTo(REQUEST.content())
            );
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 삭제")
    class deleteByCommentId {
        @Test
        @DisplayName("작성자가 아니라면 댓글을 삭제할 수 없다")
        void memberIsNotWriter() {
            // given
            Long savedCommentId = noticeCommentService.register(notice.getId(), member.getId(), REQUEST);

            // when - then
            assertThatThrownBy(() -> noticeCommentService.remove(savedCommentId, host.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 삭제에 성공한다")
        void success() {
            // given
            Long savedCommentId = noticeCommentService.register(notice.getId(), member.getId(), REQUEST);

            // when
            noticeCommentService.remove(savedCommentId, member.getId());

            // then
            assertThat(commentRepository.existsById(savedCommentId)).isFalse();
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 수정")
    class update {
        @Test
        @DisplayName("작성자가 아니라면 댓글을 수정할 수 없다")
        void memberIsNotWriter() {
            // given
            NoticeCommentRequest UPDATE_REQUEST = NoticeCommentRequest.builder()
                    .content("변경되었습니다!")
                    .build();
            Long savedCommentId = commentRepository.save(Comment.writeComment(notice, host, "댓글입니다!")).getId();

            // when - then
            assertThatThrownBy(() -> noticeCommentService.update(savedCommentId, host.getId() + 100L, UPDATE_REQUEST))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("공지사항에 대한 댓글 수정에 성공한다")
        void success() {
            // given
            NoticeCommentRequest UPDATE_REQUEST = NoticeCommentRequest.builder()
                    .content("변경되었습니다!")
                    .build();
            Long savedCommentId = noticeCommentService.register(notice.getId(), host.getId(), REQUEST);

            // when - then
            noticeCommentService.update(savedCommentId, host.getId(), UPDATE_REQUEST);
            Comment comment = noticeCommentService.findById(savedCommentId);

            assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.content());
        }
    }
}