package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.service.dto.response.ReviewAssembler;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
import com.kgu.studywithme.study.service.dto.response.StudyReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationService {
    private final StudyFindService studyFindService;

    public StudyInformation getInformation(Long studyId) {
        Study study = studyFindService.findByIdWithHostAndParticipant(studyId);
        return new StudyInformation(study);
    }

    public ReviewAssembler getReviews(Long studyId) {
        Study study = studyFindService.findByIdWithReviews(studyId);
        int graduateCount = study.getGraduatedParticipants().size();
        List<StudyReview> reviews = study.getReviews()
                .stream()
                .map(StudyReview::new)
                .toList();

        return new ReviewAssembler(graduateCount, reviews);
    }
}
