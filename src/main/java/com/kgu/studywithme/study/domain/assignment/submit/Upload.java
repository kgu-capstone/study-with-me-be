package com.kgu.studywithme.study.domain.assignment.submit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.LINK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Upload {
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false)
    private UploadType type;

    private Upload(String link, UploadType type) {
        this.link = link;
        this.type = type;
    }

    public static Upload withLink(String link) {
        return new Upload(link, LINK);
    }

    public static Upload withFile(String link) {
        return new Upload(link, FILE);
    }
}
