package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyFindService {
    private final StudyRepository studyRepository;

    public Study findById(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdWithParticipants(Long studyId) {
        return studyRepository.findByIdWithParticipants(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdWithHost(Long studyId) {
        return studyRepository.findByIdWithHost(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdAndHostId(Long studyId, Long hostId) {
        return studyRepository.findByIdAndHostId(studyId, hostId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdAndHostIdWithParticipants(Long studyId, Long hostId) {
        return studyRepository.findByIdAndHostIdWithParticipants(studyId, hostId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }
}
