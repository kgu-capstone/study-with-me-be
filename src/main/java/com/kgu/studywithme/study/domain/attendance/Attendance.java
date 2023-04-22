package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "week", nullable = false)
    private int week;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", referencedColumnName = "id", nullable = false)
    private Member participant;

    private Attendance(Study study, Member participant, int week, AttendanceStatus status) {
        this.study = study;
        this.participant = participant;
        this.week = week;
        this.status = status;
    }

    public static Attendance recordAttendance(Study study, Member participant, int week, AttendanceStatus status) {
        return new Attendance(study, participant, week, status);
    }

    public void updateAttendanceStatus(String status) {
        this.status = AttendanceStatus.fromDescription(status);
    }
}
