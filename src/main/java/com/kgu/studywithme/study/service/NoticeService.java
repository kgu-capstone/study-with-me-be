package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.exception.NoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final CommentRepository commentRepository;
    private final StudyFindService studyFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public Long register(Long studyId, NoticeRequest request, Long hostId) {
        validateHost(studyId, hostId);
        Study study = studyFindService.findByIdWithHost(studyId);
        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .study(study)
                .build();

        return noticeRepository.save(notice).getId();
    }

    @Transactional
    public void remove(Long studyId, Long noticeId, Long hostId) {
        validateHost(studyId, hostId);
        validateNoticeWriter(noticeId, hostId);

        commentRepository.deleteByNoticeId(noticeId);
        noticeRepository.deleteById(noticeId);
    }

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    public Notice findByIdWithStudy(Long noticeId) {
        return noticeRepository.findByIdWithStudy(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    private void validateHost(Long studyId, Long memberId) {
        studyValidator.validateHost(studyId, memberId);
    }

    private void validateNoticeWriter(Long noticeId, Long memberId) {
        studyValidator.validateNoticeWriter(noticeId, memberId);
    }
}