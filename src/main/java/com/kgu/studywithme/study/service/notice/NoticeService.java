package com.kgu.studywithme.study.service.notice;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.StudyFindService;
import com.kgu.studywithme.study.service.StudyValidator;
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
    public Long register(Long studyId, String title, String content) {
        Study study = studyFindService.findByIdWithHost(studyId);
        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .study(study)
                .build();

        return noticeRepository.save(notice).getId();
    }

    @Transactional
    public void remove(Long noticeId, Long hostId) {
        validateNoticeWriter(noticeId, hostId);

        commentRepository.deleteByNoticeId(noticeId);
        noticeRepository.deleteById(noticeId);
    }

    @Transactional
    public void update(Long noticeId, Long hostId, String title, String content) {
        validateNoticeWriter(noticeId, hostId);

        Notice notice = findById(noticeId);
        notice.updateNoticeInformation(title, content);
    }

    private void validateNoticeWriter(Long noticeId, Long memberId) {
        studyValidator.validateNoticeWriter(noticeId, memberId);
    }

    private Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.NOTICE_NOT_FOUND));
    }
}
