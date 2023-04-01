package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationService {
    private final StudyFindService studyFindService;

    public StudyInformation getInformation(Long studyId) {
        Study study = studyFindService.findByIdWithHostAndParticipant(studyId);
        return new StudyInformation(study);
    }
}
