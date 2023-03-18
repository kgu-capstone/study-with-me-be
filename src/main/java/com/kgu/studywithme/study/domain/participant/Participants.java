package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Participants {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    private Member host;

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Participant> participants = new ArrayList<>();

    @Embedded
    private Capacity capacity;

    private Participants(Member host, Capacity capacity) {
        this.host = host;
        this.capacity = capacity;
    }

    public static Participants of(Member host, Capacity capacity) {
        return new Participants(host, capacity);
    }

    public void apply(Study study, Member member) {
        validateMemberIsNotHost(member);
        validateMemberIsNotApplierAndParticipant(member);
        participants.add(Participant.applyInStudy(study, member));
    }

    public void approve(Member member) {
        validateMemberIsApplier(member);
        validateStudyCapacityIsNotYetFull();
        updateMemberParticipationStatus(member, APPROVE);
    }

    public void reject(Member member) {
        validateMemberIsApplier(member);
        updateMemberParticipationStatus(member, REJECT);
    }

    public void cancel(Member participant) {
        validateMemberIsParticipant(participant);
        updateMemberParticipationStatus(participant, CALCEL);
    }

    private void updateMemberParticipationStatus(Member member, ParticipantStatus status) {
        participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .forEach(participant -> participant.updateStatus(status));
    }

    private void validateMemberIsNotHost(Member member) {
        if (host.isSameMember(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_HOST);
        }
    }

    private void validateMemberIsNotApplierAndParticipant(Member member) {
        if (isApplierOrParticipant(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_PARTICIPANT);
        }
    }

    private boolean isApplierOrParticipant(Member member) {
        return participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .anyMatch(participant -> participant.getStatus() == APPLY || participant.getStatus() == APPROVE);
    }

    private void validateMemberIsApplier(Member member) {
        if (!isApplier(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_APPLIER);
        }
    }

    private boolean isApplier(Member member) {
        return participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .anyMatch(participant -> participant.getStatus() == APPLY);
    }

    private void validateStudyCapacityIsNotYetFull() {
        if (isFull()) {
            throw StudyWithMeException.type(StudyErrorCode.STUDY_CAPACITY_IS_FULL);
        }
    }

    private boolean isFull() {
        return capacity.isEqualOrOver(getNumberOfApproveParticipants());
    }

    private void validateMemberIsParticipant(Member participant) {
        if (!isParticipant(participant)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT);
        }
    }

    private boolean isParticipant(Member member) {
        return getApproveParticipants().stream()
                .anyMatch(participant -> participant.isSameMember(member));
    }

    public List<Member> getParticipants() {
        List<Member> members = participants.stream()
                .map(Participant::getMember)
                .toList();

        return Stream.of(List.of(host), members)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Member> getApproveParticipants() {
        List<Member> members = participants.stream()
                .filter(participant -> participant.getStatus() == APPROVE)
                .map(Participant::getMember)
                .toList();

        return Stream.of(List.of(host), members)
                .flatMap(Collection::stream)
                .toList();
    }

    private int getNumberOfApproveParticipants() {
        return getApproveParticipants().size();
    }
}
