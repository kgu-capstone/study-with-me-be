package com.kgu.studywithme.study.domain.assignment.submit;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.assignment.Assignment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_assignment_submit")
public class Submit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Upload upload;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", referencedColumnName = "id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", referencedColumnName = "id", nullable = false)
    private Member participant;

    @Builder
    private Submit(Assignment assignment, Member participant, Upload upload) {
        this.assignment = assignment;
        this.participant = participant;
        this.upload = upload;
    }

    public static Submit submitAssignment(Assignment assignment, Member participant, Upload upload) {
        return new Submit(assignment, participant, upload);
    }
}
