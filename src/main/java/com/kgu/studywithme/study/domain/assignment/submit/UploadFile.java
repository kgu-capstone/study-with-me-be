package com.kgu.studywithme.study.domain.assignment.submit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UploadFile {
    @Column(name = "upload_name")
    private String uploadName;

    @Column(name = "storage_name", unique = true)
    private String storageName;

    private UploadFile(String uploadName, String storageName) {
        this.uploadName = uploadName;
        this.storageName = storageName;
    }

    public static UploadFile of(String uploadName, String storageName) {
        return new UploadFile(uploadName, storageName);
    }

    public static UploadFile from(MultipartFile file) {
        String uploadName = getOriginalFileName(file);
        String storageName = generateStorageName(uploadName);
        return new UploadFile(uploadName, storageName);
    }

    private static String getOriginalFileName(MultipartFile file) {
        return file.getOriginalFilename();
    }

    private static String generateStorageName(@NotNull String uploadName) {
        String findName = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
        String extension = uploadName.substring(uploadName.lastIndexOf(".") + 1);
        return findName + "." + extension;
    }
}
