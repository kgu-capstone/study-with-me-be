package com.kgu.studywithme.study.service;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.domain.participant.Capacity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kgu.studywithme.study.domain.RecruitmentStatus.COMPLETE;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {
    private final StudyValidator studyValidator;
    private final StudyRepository studyRepository;
    private final MemberFindService memberFindService;
    private final StudyFindService studyFindService;

    @Transactional
    public Long register(StudyRegisterRequest request, Long hostId) {
        validateUniqueNameForCreate(request.name());

        Member host = memberFindService.findById(hostId);
        Study study = buildStudy(request, host);

        return studyRepository.save(study).getId();
    }

    @Transactional
    public void update(Long studyId, Long hostId, StudyUpdateRequest request) {
        validateUniqueNameForUpdate(request.name(), studyId);
        validateHost(studyId, hostId);

        Study study = studyFindService.findById(studyId);
        study.update(
                StudyName.from(request.name()),
                Description.from(request.description()),
                request.capacity(),
                Category.from(request.category()),
                request.type().equals(ONLINE.getDescription()) ? ONLINE : OFFLINE,
                request.province(),
                request.city(),
                request.recruitmentStatus() ? IN_PROGRESS : COMPLETE,
                request.hashtags()
        );
    }

    private void validateUniqueNameForCreate(String name) {
        studyValidator.validateUniqueNameForCreate(StudyName.from(name));
    }

    private void validateUniqueNameForUpdate(String name, Long studyId) {
        studyValidator.validateUniqueNameForUpdate(StudyName.from(name), studyId);
    }

    private void validateHost(Long studyId, Long memberId) {
        studyValidator.validateHost(studyId, memberId);
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
