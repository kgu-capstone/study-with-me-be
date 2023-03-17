package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPLY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "participant")
public class Participant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    private Participant(Study study, Member member, ParticipantStatus status) {
        this.study = study;
        this.member = member;
        this.status = status;
    }

    public static Participant applyInStudy(Study study, Member member) {
        return new Participant(study, member, APPLY);
    }
}
