package com.kgu.studywithme.upload.utils;

public interface BucketMetadata {
    /**
     * Weekly 글 내부 이미지 업로드 버킷 -> images/{studyId}-{week}-{memberId}-{upload file name}
     */
    String WEEKLY_IMAGES = "images/%d-%d-%d-%s";

    /**
     * Weekly 글 첨부파일 업로드 버킷 -> attachments/{studyId}-{week}-{memberId}-{upload file name}
     */
    String WEEKLY_ATTACHMENTS = "attachments/%d-%d-%d-%s";

    /**
     * Weekly 과제 제출 버킷 -> submits/{studyId}-{week}-{memberId}-{upload file name}
     */
    String WEEKLY_SUBMITS = "submits/%d-%d-%d-%s";
}
