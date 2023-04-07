package com.kgu.studywithme.study.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationService {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void apply(Long studyId, Long memberId) {
        Study study = studyFindService.findById(studyId);
        Member member = memberFindService.findById(memberId);

        study.applyParticipation(member);
    }

    @Transactional
    public void applyCancel(Long studyId, Long applierId) {
        Study study = studyFindService.findById(studyId);
        Member applier = memberFindService.findById(applierId);
        study.validateMemberIsApplier(applier);

        participantRepository.deleteApplier(study, applier);
    }

    @Transactional
    public void approve(Long studyId, Long applierId, Long hostId) {
        Study study = studyFindService.findByIdAndHostId(studyId, hostId);
        Member applier = memberFindService.findById(applierId);

        study.approveParticipation(applier);
    }

    @Transactional
    public void reject(Long studyId, Long applierId, Long hostId) {
        Study study = studyFindService.findByIdAndHostId(studyId, hostId);
        Member applier = memberFindService.findById(applierId);

        study.rejectParticipation(applier);
    }

    @Transactional
    public void cancel(Long studyId, Long participantId) {
        Study study = studyFindService.findById(studyId);
        Member participant = memberFindService.findById(participantId);

        study.cancelParticipation(participant);
    }

    @Transactional
    public void delegateAuthority(Long studyId, Long participantId, Long hostId) {
        Study study = studyFindService.findByIdAndHostId(studyId, hostId);
        Member newHost = memberFindService.findById(participantId);

        study.delegateStudyHostAuthority(newHost);
    }

    @Transactional
    public void graduate(Long studyId, Long participantId) {
        Study study = studyFindService.findById(studyId);
        Member participant = memberFindService.findById(participantId);

        study.graduateParticipant(participant);
    }
}
