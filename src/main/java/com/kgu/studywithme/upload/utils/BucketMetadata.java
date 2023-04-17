package com.kgu.studywithme.upload.utils;

public interface BucketMetadata {
    /**
     * Weekly 글 내부 이미지 업로드 버킷 -> images/{UUID}-{upload file name}
     */
    String WEEKLY_IMAGES = "images/%s";

    /**
     * Weekly 글 첨부파일 업로드 버킷 -> attachments/{UUID}-{upload file name}
     */
    String WEEKLY_ATTACHMENTS = "attachments/%s";

    /**
     * Weekly 과제 제출 버킷 -> submits/{UUID}-{upload file name}
     */
    String WEEKLY_SUBMITS = "submits/%s";
}
