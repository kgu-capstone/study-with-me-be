package com.kgu.studywithme.study.domain.assignment;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.assignment.submit.Submit;
import com.kgu.studywithme.study.domain.assignment.submit.Upload;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "assignment")
public class Assignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "week", nullable = false)
    private int week;

    @Embedded
    private Period period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    private Member creator;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.PERSIST)
    private List<Submit> submits = new ArrayList<>();

    @Builder
    private Assignment(int week, Period period, Study study, Member creator, String title, String content) {
        this.week = week;
        this.period = period;
        this.study = study;
        this.creator = creator;
        this.title = title;
        this.content = content;
    }

    public static Assignment createAssignment(int week, Period period, Study study, Member creator, String title, String content) {
        return new Assignment(week, period, study, creator, title, content);
    }

    public void submit(Member participant, Upload upload) {
        submits.add(Submit.submitAssignment(this, participant, upload));
    }
}
