package com.kgu.studywithme.upload.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.upload.exception.UploadErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.kgu.studywithme.upload.utils.BucketMetadata.*;

@Slf4j
@Component
public class FileUploader {
    private static final String DESCRIPTION = "description";
    private static final String IMAGE = "image";
    private static final String ATTACHMENT = "attachment";
    private static final String SUBMIT = "submit";

    private final AmazonS3 amazonS3;
    private final String bucket;

    public FileUploader(AmazonS3 amazonS3,
                        @Value("${cloud.ncp.storage.bucket}") String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    // 스터디 생성 시 설명 내부 이미지 업로드
    public String uploadStudyDescriptionImage(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(DESCRIPTION, file);
    }

    // Weekly 글 내부 이미지 업로드
    public String uploadWeeklyImage(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(IMAGE, file);
    }

    // Weekly 글 첨부파일 업로드
    public String uploadWeeklyAttachment(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(ATTACHMENT, file);
    }

    // Weekly 과제 제출
    public String uploadWeeklySubmit(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(SUBMIT, file);
    }

    private void validateFileExists(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw StudyWithMeException.type(UploadErrorCode.FILE_IS_EMPTY);
        }
    }

    private String uploadFile(String type, MultipartFile file) {
        String fileName = createFileNameByType(type, file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage());
            throw StudyWithMeException.type(UploadErrorCode.S3_UPLOAD_FAILURE);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileNameByType(String type, String originalFileName) {
        String fileName = UUID.randomUUID() + extractFileExtension(originalFileName);

        return switch (type) {
            case DESCRIPTION -> String.format(STUDY_DESCRIPTIONS, fileName);
            case IMAGE -> String.format(WEEKLY_IMAGES, fileName);
            case ATTACHMENT -> String.format(WEEKLY_ATTACHMENTS, fileName);
            default -> String.format(WEEKLY_SUBMITS, fileName);
        };
    }

    private String extractFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
