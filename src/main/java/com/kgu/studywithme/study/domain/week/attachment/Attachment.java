package com.kgu.studywithme.study.domain.week.attachment;

import com.kgu.studywithme.study.domain.week.Week;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "link", nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", referencedColumnName = "id", nullable = false)
    private Week week;

    @Builder
    private Attachment(Week week, String link) {
        this.week = week;
        this.link = link;
    }

    public static Attachment addAttachmentFile(Week week, String link) {
        return new Attachment(week, link);
    }
}
