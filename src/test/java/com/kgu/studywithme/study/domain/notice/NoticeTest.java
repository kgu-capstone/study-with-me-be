package com.kgu.studywithme.study.domain.notice;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Notice 도메인 테스트")
class NoticeTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);

    @Test
    @DisplayName("Notice를 생성한다")
    void construct() {
        Notice notice = Notice.writeNotice(STUDY, "Notice 1", "Hello World");

        assertAll(
                () -> assertThat(notice.getTitle()).isEqualTo("Notice 1"),
                () -> assertThat(notice.getContent()).isEqualTo("Hello World"),
                () -> assertThat(notice.getStudy()).isEqualTo(STUDY),
                () -> assertThat(notice.getWriter()).isEqualTo(HOST)
        );
    }
    
    @Test
    @DisplayName("공지사항 제목 & 내용을 수정한다")
    void updateNoticeInformation() {
        // given
        Notice notice = Notice.writeNotice(STUDY, "Notice 1", "Hello World");
        
        // when
        notice.updateNoticeInformation("Notice 2", "Hello World222");
        
        // then
        assertAll(
                () -> assertThat(notice.getTitle()).isEqualTo("Notice 2"),
                () -> assertThat(notice.getContent()).isEqualTo("Hello World222")
        );
    }

    @Test
    @DisplayName("공지사항에 댓글을 작성한다")
    void addComment() {
        // given
        Notice notice = Notice.writeNotice(STUDY, "Notice 1", "Hello World");

        // when
        notice.addComment(HOST, "댓글 1");
        notice.addComment(HOST, "댓글 2");
        notice.addComment(PARTICIPANT, "댓글 3");
        notice.addComment(PARTICIPANT, "댓글 4");
        notice.addComment(PARTICIPANT, "댓글 5");

        // then
        assertAll(
                () -> assertThat(notice.getComments()).hasSize(5),
                () -> assertThat(notice.getComments())
                        .map(Comment::getContent)
                        .containsExactlyInAnyOrder("댓글 1", "댓글 2", "댓글 3", "댓글 4", "댓글 5"),
                () -> assertThat(notice.getComments())
                        .map(Comment::getWriter)
                        .containsExactlyInAnyOrder(HOST, HOST, PARTICIPANT, PARTICIPANT, PARTICIPANT)
        );
    }
}
