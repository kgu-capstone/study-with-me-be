package com.kgu.studywithme.upload.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DisplayName("Upload [Utils] -> AmazonS3Properties 테스트")
class AmazonS3PropertiesTest {
    @Autowired
    private AmazonS3Properties properties;

    @Test
    @DisplayName("Amazon S3 Properties가 yml에 있는 값을 제대로 읽어내는지 확인한다")
    void check() {
        assertAll(
                () -> assertThat(properties.getRegion()).isEqualTo("kr-standard"),
                () -> assertThat(properties.getEndPoint()).isEqualTo("kr.object.ncloudstorage.com"),
                () -> assertThat(properties.getAccessKey()).isEqualTo("access-key"),
                () -> assertThat(properties.getSecretKey()).isEqualTo("secret-key"),
                () -> assertThat(properties.getBucket()).isEqualTo("bucket-name")
        );
    }
}
