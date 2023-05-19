package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import com.kgu.studywithme.study.event.StudyApprovedEvent;
import com.kgu.studywithme.study.event.StudyGraduatedEvent;
import com.kgu.studywithme.study.event.StudyRejectedEvent;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationService {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void apply(Long studyId, Long memberId) {
        Study study = studyFindService.findByIdWithParticipants(studyId);
        Member member = memberFindService.findById(memberId);

        study.applyParticipation(member);
    }

    @Transactional
    public void applyCancel(Long studyId, Long applierId) {
        Study study = studyFindService.findByIdWithParticipants(studyId);
        Member applier = memberFindService.findById(applierId);
        study.validateMemberIsApplier(applier);

        participantRepository.deleteApplier(study, applier);
    }

    @Transactional
    public void approve(Long studyId, Long applierId, Long hostId) {
        Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        Member applier = memberFindService.findById(applierId);

        study.approveParticipation(applier);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyApprovedEvent(
                            applier.getEmailValue(),
                            applier.getNicknameValue(),
                            study.getNameValue()
                    )
            );
        }
    }

    @Transactional
    public void reject(Long studyId, Long applierId, Long hostId, String reason) {
        Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        Member applier = memberFindService.findById(applierId);

        study.rejectParticipation(applier);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyRejectedEvent(
                            applier.getEmailValue(),
                            applier.getNicknameValue(),
                            study.getNameValue(),
                            reason
                    )
            );
        }
    }

    @Transactional
    public void cancel(Long studyId, Long participantId) {
        Study study = studyFindService.findByIdWithParticipants(studyId);
        Member participant = memberFindService.findById(participantId);

        study.cancelParticipation(participant);
    }

    @Transactional
    public void delegateAuthority(Long studyId, Long participantId, Long hostId) {
        Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        Member newHost = memberFindService.findById(participantId);

        study.delegateStudyHostAuthority(newHost);
    }

    @Transactional
    public void graduate(Long studyId, Long participantId) {
        Study study = studyFindService.findByIdWithParticipants(studyId);
        Member participant = memberFindService.findById(participantId);
        validateGraduationRequirements(study, participantId);

        study.graduateParticipant(participant);

        if (participant.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyGraduatedEvent(
                            participant.getEmailValue(),
                            participant.getNicknameValue(),
                            study.getNameValue()
                    )
            );
        }
    }

    private void validateGraduationRequirements(Study study, Long memberId) {
        int attendanceCount = memberRepository.getAttendanceCount(study.getId(), memberId, ATTENDANCE).intValue();

        if (!study.isGraduationRequirementsFulfilled(attendanceCount)) {
            throw StudyWithMeException.type(StudyErrorCode.GRADUATION_REQUIREMENTS_NOT_FULFILLED);
        }
    }
}
