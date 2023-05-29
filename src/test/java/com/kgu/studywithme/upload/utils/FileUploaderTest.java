package com.kgu.studywithme.upload.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.kgu.studywithme.common.InfraTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.upload.exception.UploadErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Upload [Utils] -> FileUploader 테스트")
class FileUploaderTest extends InfraTest {
    private FileUploader uploader;

    @Mock
    private AmazonS3 amazonS3;
    private static final String BUCKET = "bucket";
    private static final String IMAGE = "images";
    private static final String ATTACHMENT = "attachments";
    private static final String SUBMIT = "submits";

    @BeforeEach
    void setUp() {
        uploader = new FileUploader(amazonS3, BUCKET);
    }

    @Nested
    @DisplayName("스터디 생성 시 설명 내부 이미지 업로드")
    class uploadDescriptionImage {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "hello.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadStudyDescriptionImage(nullFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadStudyDescriptionImage(emptyFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("이미지 업로드를 성공한다")
        void success() throws Exception {
            // given
            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(createUploadLink(IMAGE, "hello4.png"));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            String uploadUrl = uploader.uploadStudyDescriptionImage(file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrl).isEqualTo(mockUrl.toString());
        }
    }

    @Nested
    @DisplayName("Weekly 글 내부 이미지 업로드")
    class uploadWeeklyImage {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "hello.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadWeeklyImage(nullFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadWeeklyImage(emptyFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("이미지 업로드를 성공한다")
        void success() throws Exception {
            // given
            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(createUploadLink(IMAGE, "hello4.png"));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            String uploadUrl = uploader.uploadWeeklyImage(file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrl).isEqualTo(mockUrl.toString());
        }
    }

    @Nested
    @DisplayName("Weekly 글 첨부파일 업로드")
    class uploadWeeklyAttachments {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "hello.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadWeeklyAttachment(nullFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadWeeklyAttachment(emptyFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("첨부파일 업로드를 성공한다")
        void success() throws Exception {
            // given
            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(createUploadLink(ATTACHMENT, "hello1.txt"));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            String uploadUrl = uploader.uploadWeeklyAttachment(file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrl).isEqualTo(mockUrl.toString());
        }
    }

    @Nested
    @DisplayName("Weekly 과제 제출")
    class uploadWeeklySubmit {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadWeeklySubmit(nullFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadWeeklySubmit(emptyFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("과제 업로드를 성공한다")
        void success() throws Exception {
            // given
            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(createUploadLink(SUBMIT, "hello3.pdf"));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
            String uploadUrl = uploader.uploadWeeklySubmit(file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrl).isEqualTo(mockUrl.toString());
        }
    }

    @Test
    @DisplayName("NCP Object Storage와의 통신 간 네트워크적인 오류가 발생한다")
    void throwExceptionByNCPCommunications() throws IOException {
        // given
        doThrow(StudyWithMeException.type(UploadErrorCode.S3_UPLOAD_FAILURE))
                .when(amazonS3)
                .putObject(any(PutObjectRequest.class));

        // when
        final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        assertThatThrownBy(() -> uploader.uploadWeeklySubmit(file))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(UploadErrorCode.S3_UPLOAD_FAILURE.getMessage());

        // then
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(0)).getUrl(eq(BUCKET), anyString());
    }

    private String createUploadLink(String type, String originalFileName) {
        return String.format(
                "https://kr.object.ncloudstorage.com/%s/%s/%s",
                BUCKET,
                type,
                UUID.randomUUID() + extractFileExtension(originalFileName)
        );
    }

    private String extractFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
