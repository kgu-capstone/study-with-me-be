package com.kgu.studywithme.study.domain.week.submit;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.week.Week;
import lombok.AccessLevel;
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
    private Week week;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", referencedColumnName = "id", nullable = false)
    private Member participant;

    private Submit(Week week, Member participant, Upload upload) {
        this.week = week;
        this.participant = participant;
        this.upload = upload;
    }

    public static Submit submitAssignment(Week week, Member participant, Upload upload) {
        return new Submit(week, participant, upload);
    }

    public void editUpload(Upload upload) {
        this.upload = upload;
    }
}
