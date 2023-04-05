package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.exception.CommentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeCommentService {
    private final NoticeService noticeService;
    private final MemberFindService memberFindService;
    private final CommentRepository commentRepository;
    private final StudyValidator studyValidator;

    @Transactional
    public void register(Long noticeId, Long memberId, String content) {
        Notice notice = noticeService.findByIdWithStudy(noticeId);
        Member writer = memberFindService.findById(memberId);
        validateWriterIsParticipant(notice, writer);

        notice.addComment(writer, content);
    }

    @Transactional
    public void remove(Long commentId, Long memberId) {
        validateCommentWriter(commentId, memberId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void update(Long commentId, Long memberId, String content) {
        validateCommentWriter(commentId, memberId);
        Comment comment = findById(commentId);
        comment.updateComment(content);
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> StudyWithMeException.type(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateWriterIsParticipant(Notice notice, Member writer) {
        Study study = notice.getStudy();
        study.validateMemberIsParticipant(writer);
    }

    private void validateCommentWriter(Long commentId, Long memberId) {
        studyValidator.validateCommentWriter(commentId, memberId);
    }
}