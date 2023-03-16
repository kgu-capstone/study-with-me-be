package com.kgu.studywithme.study.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_attendance")
public class StudyAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudyLog studyLog;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudyMember studyMember;

    @Column(name = "attendance")
    private boolean isAttendance;

    // 이거를 과제를 담는 엔티티로 사용해도 될거 같지 않나요?
    // 일단 String으로 했어요 (파일, 사진 등..)
    private String content;

    @Builder
    public StudyAttendance(StudyLog studyLog, StudyMember studyMember, boolean isAttendance, String content) {
        this.studyLog = studyLog;
        this.studyMember = studyMember;
        this.isAttendance = isAttendance;
        this.content = content;
    }

    public static StudyAttendance createStudyAttendance(StudyLog studyLog, StudyMember studyMember, boolean isAttendance, String content) {
        return new StudyAttendance(studyLog, studyMember, isAttendance, content);
    }
}
