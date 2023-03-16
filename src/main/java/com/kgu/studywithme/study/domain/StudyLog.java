package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_log")
public class StudyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", unique = true)
    private Study study;

    // 여기에 게시글로 사람들의 활동을 추가하면 될 것 같아요.
    @OneToMany(mappedBy = "studyLog")
    private ArrayList<StudyAttendance> studyAttendanceList = new ArrayList<>();

    @Builder
    public StudyLog(String title, String content, Study study) {
        this.title = title;
        this.content = content;
        this.study = study;

        this.date = LocalDate.now();
    }

    public static StudyLog createStudyLog(String title, String content, Study study) {
        if (study.getStatus() == StudyStatus.END)
            throw StudyWithMeException.type(StudyErrorCode.STUDY_ALREADY_END);

        return new StudyLog(title, content, study);
    }

    public void addStudyAttendance(StudyAttendance studyAttendance) {
        this.studyAttendanceList.add(studyAttendance);
    }
}
