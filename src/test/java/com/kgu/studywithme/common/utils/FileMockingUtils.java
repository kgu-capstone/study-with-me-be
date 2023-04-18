package com.kgu.studywithme.common.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

public class FileMockingUtils {
    private static final String FILE_PATH = "src/test/resources/files/";
    private static final String SINGLE_FILE_META_NAME = "file";
    private static final String MULTIPLE_FILE_META_NAME = "files";

    public static MultipartFile createSingleMockMultipartFile(String fileName, String contentType) throws IOException {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile(SINGLE_FILE_META_NAME, fileName, contentType, stream);
        }
    }

    public static MultipartFile createMultipleMockMultipartFile(String fileName, String contentType) throws IOException {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile(MULTIPLE_FILE_META_NAME, fileName, contentType, stream);
        }
    }
}
