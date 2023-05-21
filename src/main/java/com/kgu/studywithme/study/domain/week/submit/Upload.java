package com.kgu.studywithme.study.domain.week.submit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Upload {
    @Column(name = "update_file_name")
    private String uploadFileName;

    @Column(name = "link", nullable = false)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false)
    private UploadType type;

    private Upload(String uploadFileName, String link, UploadType type) {
        this.uploadFileName = uploadFileName;
        this.link = link;
        this.type = type;
    }

    public static Upload withLink(String link) {
        return new Upload(null, link, LINK);
    }

    public static Upload withFile(String uploadFileName, String link) {
        return new Upload(uploadFileName, link, FILE);
    }
}
