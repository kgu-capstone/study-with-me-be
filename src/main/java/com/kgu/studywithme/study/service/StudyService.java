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
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @Transactional
    public Long register(Long hostId, StudyRegisterRequest request) {
        validateUniqueNameForCreate(request.name());

        Member host = memberFindService.findById(hostId);
        Study study = buildStudy(request, host);

        return studyRepository.save(study).getId();
    }

    private void validateUniqueNameForCreate(String name) {
        studyValidator.validateUniqueNameForCreate(StudyName.from(name));
    }

    private Study buildStudy(StudyRegisterRequest request, Member host) {
        if (request.type().equalsIgnoreCase(ONLINE.getBrief())) {
            return Study.createOnlineStudy(
                    host,
                    StudyName.from(request.name()),
                    Description.from(request.description()),
                    Capacity.from(request.capacity()),
                    Category.from(request.category()),
                    StudyThumbnail.from(request.thumbnail()),
                    request.minimumAttendanceForGraduation(),
                    request.hashtags()
            );
        } else {
            return Study.createOfflineStudy(
                    host,
                    StudyName.from(request.name()),
                    Description.from(request.description()),
                    Capacity.from(request.capacity()),
                    Category.from(request.category()),
                    StudyThumbnail.from(request.thumbnail()),
                    StudyLocation.of(request.province(), request.city()),
                    request.minimumAttendanceForGraduation(),
                    request.hashtags()
            );
        }
    }

    @Transactional
    public void update(Long studyId, Long hostId, StudyUpdateRequest request) {
        validateUniqueNameForUpdate(request.name(), studyId);

        Study study = studyFindService.findByIdAndHostId(studyId, hostId);
        study.update(
                StudyName.from(request.name()),
                Description.from(request.description()),
                request.capacity(),
                Category.from(request.category()),
                StudyThumbnail.from(request.thumbnail()),
                request.type().equalsIgnoreCase(ONLINE.getBrief()) ? ONLINE : OFFLINE,
                request.province(),
                request.city(),
                request.recruitmentStatus() ? IN_PROGRESS : COMPLETE,
                request.hashtags()
        );
    }

    private void validateUniqueNameForUpdate(String name, Long studyId) {
        studyValidator.validateUniqueNameForUpdate(StudyName.from(name), studyId);
    }

    @Transactional
    public void close(Long studyId) {
        Study study = studyFindService.findById(studyId);
        study.close();
    }
}
