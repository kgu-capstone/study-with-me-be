package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.exception.CommentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final StudyFindService studyFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public void register(Long studyId, NoticeRequest request, Long memberId) {
        Study study = studyFindService.findByIdWithHost(studyId);
        validateHost(study, memberId);

        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .study(study)
                .build();

        noticeRepository.save(notice);
    }

    @Transactional
    public void remove(Long studyId, Long noticeId, Long memberId) {
        Study study = studyFindService.findByIdWithHost(studyId);
        Notice notice = findById(noticeId);

        validateHost(study, memberId);
        studyValidator.validateNoticeWriter(notice, memberId);

        noticeRepository.delete(notice);
    }

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(CommentErrorCode.COMMENT_NOT_FOUND));

    }

    private void validateHost(Study study, Long memberId) {
        studyValidator.validateHost(study, memberId);
    }
}