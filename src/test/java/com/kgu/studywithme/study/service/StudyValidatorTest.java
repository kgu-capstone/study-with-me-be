package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study [Service Layer] -> StudyValidator 테스트")
class StudyValidatorTest extends ServiceTest {
    @Autowired
    private StudyValidator studyValidator;

    private Member host;
    private Study study;
    private Notice notice;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(TOEIC.toOnlineStudy(host));
        notice = noticeRepository.save(Notice.writeNotice(study, "공지사항", "내용"));
    }

    @Test
    @DisplayName("스터디 이름 중복에 대한 검증을 진행한다")
    void validateName() {
        final StudyName same = study.getName();
        final StudyName diff = StudyName.from("diff" + same.getValue());

        assertThatThrownBy(() -> studyValidator.validateName(same))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());
        assertDoesNotThrow(() -> studyValidator.validateName(diff));
    }

    @Test
    @DisplayName("스터디 팀장에 대한 검증을 진행한다")
    void validateHost() {
        assertThatThrownBy(() -> studyValidator.validateHost(study.getId(), host.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.MEMBER_IS_NOT_HOST.getMessage());
        assertDoesNotThrow(() -> studyValidator.validateHost(study.getId(), host.getId()));
    }

    @Test
    @DisplayName("스터디 공지사항 작성자에 대한 검증을 진행한다")
    void validateNoticeWriter() {
        // when - then
        assertThatThrownBy(() -> studyValidator.validateNoticeWriter(notice.getId(), host.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        assertDoesNotThrow(() -> studyValidator.validateNoticeWriter(notice.getId(), host.getId()));
    }

    @Test
    @DisplayName("공지사항 댓글 작성자에 대한 검증을 진행한다")
    void validateCommentWriter() {
        // given
        Comment comment = commentRepository.save(Comment.writeComment(notice, host, "댓글"));

        // when - then
        assertThatThrownBy(() -> studyValidator.validateCommentWriter(comment.getId(), host.getId() + 100L))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.MEMBER_IS_NOT_WRITER.getMessage());
        assertDoesNotThrow(() -> studyValidator.validateCommentWriter(notice.getId(), host.getId()));
    }

    @Test
    @DisplayName("최대 수용인원이 현재 스터디 인원 수보다 적을 수 없다")
    void validateCapacity() {
        // given
        Member member = memberRepository.save(GHOST.toMember());
        study.applyParticipation(member);
        study.approveParticipation(member);

        // when - then
        assertThatThrownBy(() -> studyValidator.validateCapacity(study.getId(), 1))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_MEMBERS.getMessage());
        assertDoesNotThrow(() -> studyValidator.validateCapacity(study.getId(), 2));
    }
}