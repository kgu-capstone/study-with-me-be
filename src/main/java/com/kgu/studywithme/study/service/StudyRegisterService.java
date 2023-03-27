package com.kgu.studywithme.study.service;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.domain.participant.Capacity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyRegisterService {
    private final StudyValidator studyValidator;
    private final StudyRepository studyRepository;
    private final MemberFindService memberFindService;

    @Transactional
    public Long register(StudyRegisterRequest request, Long hostId) {
        validateUniqueFields(request);

        Member host = memberFindService.findById(hostId);
        Study study = buildStudy(request, host);

        return studyRepository.save(study).getId();
    }

    private void validateUniqueFields(StudyRegisterRequest request) {
        studyValidator.validateName(StudyName.from(request.name()));
    }

    private Study buildStudy(StudyRegisterRequest request, Member host) {
        if (request.type().equals(ONLINE.getDescription())) {
            return Study.createOnlineStudy(
                    host,
                    StudyName.from(request.name()),
                    Description.from(request.description()),
                    Capacity.from(request.capacity()),
                    Category.from(request.category()),
                    ONLINE,
                    request.hashtags()
            );
        } else {
            return Study.createOfflineStudy(
                    host,
                    StudyName.from(request.name()),
                    Description.from(request.description()),
                    Capacity.from(request.capacity()),
                    Category.from(request.category()),
                    OFFLINE,
                    StudyArea.of(request.province(), request.city()),
                    request.hashtags()
            );
        }
    }
}
