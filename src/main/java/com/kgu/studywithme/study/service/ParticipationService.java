package com.kgu.studywithme.study.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationService {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @Transactional
    public void apply(Long studyId, Long memberId) {
        Study study = studyFindService.findByIdWithHost(studyId);
        Member member = memberFindService.findById(memberId);

        study.applyParticipation(member);
    }

    @Transactional
    public void approve(Long studyId, Long applierId, Long hostId) {
        Study study = studyFindService.findByIdAndHostId(studyId, hostId);
        Member applier = memberFindService.findById(applierId);

        study.approveParticipation(applier);
    }
}
