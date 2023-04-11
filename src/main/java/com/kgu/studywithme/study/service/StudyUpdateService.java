package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.controller.dto.request.StudyUpdate;
import com.kgu.studywithme.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyUpdateService {
    private final StudyValidator studyValidator;
    private final StudyFindService studyFindService;

    @Transactional
    public StudyUpdate getUpdateForm(Long studyId, Long hostId) {
        validateHost(studyId, hostId);

        Study study = studyFindService.findByIdWithHost(studyId);
        return StudyUpdate.buildStudyUpdate(study);
    }

    @Transactional
    public void update(Long studyId, StudyUpdate studyUpdate, Long hostId) {
        Study study = studyFindService.findById(studyId);

        validateHost(studyId, hostId);
        validateCapacity(study, studyUpdate.capacity());

        study.change(studyUpdate.name(), studyUpdate.description(), studyUpdate.capacity(), studyUpdate.category(),
                studyUpdate.type(), studyUpdate.province(), studyUpdate.city(), studyUpdate.recruitmentStatus(), studyUpdate.hashtags());
    }

    private void validateHost(Long studyId, Long memberId) {
        studyValidator.validateHost(studyId, memberId);
    }

    private void validateCapacity(Study study, Integer capacity) {
        study.validateCapacity(capacity);
    }
}
