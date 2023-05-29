package com.kgu.studywithme.study.domain.week.attachment;

import com.kgu.studywithme.study.domain.week.Week;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_week_attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UploadAttachment uploadAttachment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", referencedColumnName = "id", nullable = false)
    private Week week;

    private Attachment(Week week, UploadAttachment uploadAttachment) {
        this.week = week;
        this.uploadAttachment = uploadAttachment;
    }

    public static Attachment addAttachmentFile(Week week, UploadAttachment uploadAttachment) {
        return new Attachment(week, uploadAttachment);
    }
}
