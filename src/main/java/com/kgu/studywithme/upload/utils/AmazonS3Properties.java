package com.kgu.studywithme.upload.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AmazonS3Properties {
    private final String region;
    private final String endPoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucket;

    public AmazonS3Properties(@Value("${cloud.ncp.region}") String region,
                              @Value("${cloud.ncp.end-point}") String endPoint,
                              @Value("${cloud.ncp.access-key}") String accessKey,
                              @Value("${cloud.ncp.secret-key}") String secretKey,
                              @Value("${cloud.ncp.storage.bucket}") String bucket) {
        this.region = region;
        this.endPoint = endPoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucket;
    }
}
