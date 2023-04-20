package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.Upload;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_week")
public class Week extends BaseEntity {
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

    @Column(name = "is_assignment_exists", nullable = false)
    private boolean assignmentExists;

    @Column(name = "is_auto_attendance", nullable = false)
    private boolean autoAttendance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    private Member creator;

    @OneToMany(mappedBy = "week", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.PERSIST)
    private List<Submit> submits = new ArrayList<>();

    @Builder
    private Week(Study study, String title, String content, int week, Period period,
                 boolean assignmentExists, boolean autoAttendance, List<String> attachments) {
        this.study = study;
        this.creator = study.getHost();
        this.title = title;
        this.content = content;
        this.week = week;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        applyAttachments(attachments);
    }

    public static Week createWeek(Study study, String title, String content, int week, Period period, List<String> attachments) {
        return new Week(study, title, content, week, period, false, false, attachments);
    }

    public static Week createWeekWithAssignment(Study study, String title, String content, int week, Period period,
                                                boolean autoAttendance, List<String> attachments) {
        return new Week(study, title, content, week, period, true, autoAttendance, attachments);
    }

    private void applyAttachments(List<String> attachments) {
        if (!CollectionUtils.isEmpty(attachments)) {
            this.attachments.clear();
            this.attachments.addAll(
                    attachments.stream()
                            .map(link -> Attachment.addAttachmentFile(this, link))
                            .toList()
            );
        }
    }

    public void submitAssignment(Member participant, Upload upload) {
        submits.add(Submit.submitAssignment(this, participant, upload));
    }
}
