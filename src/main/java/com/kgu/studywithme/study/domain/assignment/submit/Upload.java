package com.kgu.studywithme.study.domain.assignment.submit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.assignment.submit.UploadType.LINK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Upload {
    private String link; // type = LINK

    @Embedded
    private UploadFile file; // type = FILE

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false)
    private UploadType type;

    private Upload(String link, UploadFile file, UploadType type) {
        this.link = link;
        this.file = file;
        this.type = type;
    }

    public static Upload withLink(String link) {
        return new Upload(link, null, LINK);
    }

    public static Upload withFile(UploadFile file) {
        return new Upload(null, file, FILE);
    }
}
