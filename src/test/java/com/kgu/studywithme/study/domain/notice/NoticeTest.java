package com.kgu.studywithme.study.domain.notice;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Notice 도메인 테스트")
class NoticeTest {
    private static final Member HOST = JIWON.toMember();
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
    @DisplayName("공지사항 제목을 수정한다")
    void updateTitle() {
        // given
        Notice notice = Notice.writeNotice(STUDY, "Notice 1", "Hello World");
        
        // when
        notice.updateTitle("Notice 2");
        
        // then
        assertThat(notice.getTitle()).isEqualTo("Notice 2");
    }
    
    @Test
    @DisplayName("공지사항 내용을 수정한다")
    void updateContent() {
        // given
        Notice notice = Notice.writeNotice(STUDY, "Notice 1", "Hello World");

        // when
        notice.updateContent("Hello World 222");

        // then
        assertThat(notice.getContent()).isEqualTo("Hello World 222");
    }

    @Test
    @DisplayName("공지사항에 댓글을 작성한다")
    void addComment() {
        // given
        Notice notice = Notice.writeNotice(STUDY, "Notice 1", "Hello World");

        // when
        notice.addComment(HOST, "댓글입니다.");

        // then
        assertAll(
                () -> assertThat(notice.getComments().size()).isEqualTo(1),
                () -> assertThat(notice.getComments().get(0))
                        .extracting("content", "writer")
                        .contains("댓글입니다.", HOST)
        );
    }
}
