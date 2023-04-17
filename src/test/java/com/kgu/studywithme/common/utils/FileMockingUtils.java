package com.kgu.studywithme.common.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

public class FileMockingUtils {
    private static final String FILE_PATH = "src/test/resources/files/";
    private static final String FILE_META_NAME = "file";

    public static MultipartFile createMockMultipartFile(String fileName, String contentType) throws IOException {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile(FILE_META_NAME, fileName, contentType, stream);
        }
    }
}
