package com.kgu.studywithme.study.service.notice;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
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

@DisplayName("Study [Service Layer] -> NoticeService 테스트")
class NoticeServiceTest extends ServiceTest {
    @Autowired
    private NoticeService noticeService;

    private Member host;
    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        study = studyRepository.save(TOEIC.toOnlineStudy(host));
        study.applyParticipation(member);
        study.approveParticipation(member);
    }

    @Nested
    @DisplayName("공지사항 등록")
    class register {
        @Test
        @DisplayName("공지사항 등록에 성공한다")
        void success() {
            // when
            Long savedNoticeId = noticeService.register(study.getId(), "제목", "내용");

            // then
            Notice findNotice = noticeRepository.findById(savedNoticeId).orElseThrow();
            assertAll(
                    () -> assertThat(findNotice.getWriter()).isEqualTo(host),
                    () -> assertThat(findNotice.getStudy()).isEqualTo(study),
                    () -> assertThat(findNotice.getTitle()).isEqualTo("제목"),
                    () -> assertThat(findNotice.getContent()).isEqualTo("내용")
            );
        }
    }
    
    @Nested
    @DisplayName("공지사항 삭제")
    class remove {
        private Notice notice;

        @BeforeEach
        void setUp() {
            notice = noticeRepository.save(Notice.writeNotice(study, "공지사항", "내용"));
        }

        @Test
        @DisplayName("작성자가 아니라면 공지사항을 삭제할 수 없다")
        void throwExceptionByMemberIsNotWriter() {
            // given
            study.delegateStudyHostAuthority(member); // 팀장(작성자) 위임

            // when - then
            assertThatThrownBy(() -> noticeService.remove(notice.getId(), member.getId()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("공지사항 삭제에 성공한다")
        void success() {
            // given
            Comment comment = commentRepository.save(Comment.writeComment(notice, member, "댓글1")); // 댓글 작성

            // when
            noticeService.remove(notice.getId(), host.getId());

            // then
            assertAll(
                    () -> assertThat(noticeRepository.existsById(notice.getId())).isFalse(),
                    () -> assertThat(commentRepository.existsById(comment.getId())).isFalse()
            );
        }
    }

    @Nested
    @DisplayName("공지사항 수정")
    class update {
        private Notice notice;

        @BeforeEach
        void setUp() {
            notice = noticeRepository.save(Notice.writeNotice(study, "공지사항", "내용"));
        }

        @Test
        @DisplayName("작성자가 아니라면 공지사항을 수정할 수 없다")
        void throwExceptionByMemberIsNotWriter() {
            // given
            study.delegateStudyHostAuthority(member); // 팀장(작성자) 위임

            // when - then
            assertThatThrownBy(() -> noticeService.update(notice.getId(), member.getId(), "공지사항22", "내용22"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        }

        @Test
        @DisplayName("공지사항 수정에 성공한다")
        void success() {
            // when
            noticeService.update(notice.getId(), host.getId(), "공지사항22", "내용22");

            // then
            Notice findNotice = noticeRepository.findById(notice.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findNotice.getWriter()).isEqualTo(host),
                    () -> assertThat(findNotice.getStudy()).isEqualTo(study),
                    () -> assertThat(findNotice.getTitle()).isEqualTo("공지사항22"),
                    () -> assertThat(findNotice.getContent()).isEqualTo("내용22")
            );
        }
    }
}
