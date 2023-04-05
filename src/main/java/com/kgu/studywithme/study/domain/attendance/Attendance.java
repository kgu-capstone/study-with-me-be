package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    private Attendance(int week, AttendanceStatus status, Study study, Member participant) {
        this.week = week;
        this.status = status;
        this.study = study;
        this.participant = participant;
    }

    public static Attendance recordAttendance(int week, AttendanceStatus status, Study study, Member participant) {
        return new Attendance(week, status, study, participant);
    }
}
