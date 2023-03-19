package com.kgu.studywithme.study.domain.notice.comment;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Notice-Comment 도메인 테스트")
class CommentTest {
    private static final Member HOST = JIWON.toMember();
    private static final Member PARTICIPANT = GHOST.toMember();
    private static final Study STUDY = SPRING.toStudy(HOST);
    private static final Notice NOTICE = Notice.writeNotice(STUDY, "Notice 1", "Hello World");

    @Test
    @DisplayName("Comment를 생성한다")
    void construct() {
        Comment comment = Comment.writeComment(NOTICE, PARTICIPANT, "안녕하세요");

        assertAll(
                () -> assertThat(comment.getNotice()).isEqualTo(NOTICE),
                () -> assertThat(comment.getWriter()).isEqualTo(PARTICIPANT),
                () -> assertThat(comment.getContent()).isEqualTo("안녕하세요")
        );
    }
    
    @Test
    @DisplayName("댓글을 수정한다")
    void updateComment() {
        // given
        Comment comment = Comment.writeComment(NOTICE, PARTICIPANT, "안녕하세요");
        
        // when
        comment.updateComment("안녕하세요 222");
        
        // then
        assertThat(comment.getContent()).isEqualTo("안녕하세요 222");
    }
}
