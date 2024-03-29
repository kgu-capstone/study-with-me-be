package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.UploadAssignment;
import lombok.AccessLevel;
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

    private Week(Study study, String title, String content, int week, Period period,
                 boolean assignmentExists, boolean autoAttendance, List<UploadAttachment> attachments) {
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

    public static Week createWeek(Study study, String title, String content,
                                  int week, Period period, List<UploadAttachment> attachments) {
        return new Week(study, title, content, week, period, false, false, attachments);
    }

    public static Week createWeekWithAssignment(Study study, String title, String content, int week, Period period,
                                                boolean autoAttendance, List<UploadAttachment> attachments) {
        return new Week(study, title, content, week, period, true, autoAttendance, attachments);
    }

    public void update(String title, String content, Period period,
                       boolean assignmentExists, boolean autoAttendance, List<UploadAttachment> attachments) {
        this.title = title;
        this.content = content;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        applyAttachments(attachments);
    }

    private void applyAttachments(List<UploadAttachment> attachments) {
        this.attachments.clear();

        if (!CollectionUtils.isEmpty(attachments)) {
            this.attachments.addAll(
                    attachments.stream()
                            .map(uploadAttachment -> Attachment.addAttachmentFile(this, uploadAttachment))
                            .toList()
            );
        }
    }

    public void submitAssignment(Member participant, UploadAssignment uploadAssignment) {
        submits.add(Submit.submitAssignment(this, participant, uploadAssignment));
    }
}
