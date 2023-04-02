package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
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
        validateHost(studyId, memberId);
        Study study = studyFindService.findByIdWithHost(studyId);

        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .study(study)
                .build();

        noticeRepository.save(notice);
    }

    @Transactional
    public void remove(Long studyId, Long noticeId, Long memberId) {
        validateHost(studyId, memberId);
        validateNoticeWriter(noticeId, memberId);

        noticeRepository.deleteById(noticeId);
    }

    private void validateHost(Long studyId, Long memberId) {
        studyValidator.validateHost(studyId, memberId);
    }

    private void validateNoticeWriter(Long noticeId, Long memberId) {
        studyValidator.validateNoticeWriter(noticeId, memberId);
    }
}