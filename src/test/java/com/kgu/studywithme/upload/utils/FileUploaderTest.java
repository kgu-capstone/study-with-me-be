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

import java.net.URL;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Upload [Utils] -> FileUploader 테스트")
class FileUploaderTest extends InfraTest {
    private FileUploader uploader;

    @Mock
    private AmazonS3 amazonS3;
    private static final String BUCKET = "bucket";
    private static final Long STUDY_ID = 1L;
    private static final Integer WEEK_1 = 1;
    private static final Long MEMBER_ID = 1L;

    @BeforeEach
    void setUp() {
        uploader = new FileUploader(amazonS3, BUCKET);
    }

    @Nested
    @DisplayName("Weekly 글 내부 이미지 업로드")
    class uploadWeeklyImage {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void fileIsNullOrEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadWeeklyImage(STUDY_ID, WEEK_1, MEMBER_ID, nullFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadWeeklyImage(STUDY_ID, WEEK_1, MEMBER_ID, emptyFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("이미지 업로드를 성공한다")
        void success() throws Exception {
            // given
            MultipartFile file = new MockMultipartFile("file", "work.txt", "text/plain", "hello world".getBytes());

            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(String.format("https://kr.object.ncloudstorage.com/%s/images/1-1-1-work.txt", BUCKET));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            String uploadUrl = uploader.uploadWeeklyImage(STUDY_ID, WEEK_1, MEMBER_ID, file);

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
        @DisplayName("첨부파일이 존재하지 않으면 빈 리스트를 응답받는다")
        void fileIsNullOrEmpty() {
            // given
            List<MultipartFile> nullFiles = null;
            List<MultipartFile> emptyFiles = List.of();

            // when
            List<String> nullFilesUrls = uploader.uploadWeeklyAttachments(STUDY_ID, WEEK_1, MEMBER_ID, nullFiles);
            List<String> emptyFilesUrls = uploader.uploadWeeklyAttachments(STUDY_ID, WEEK_1, MEMBER_ID, emptyFiles);

            // then
            assertAll(
                    () -> assertThat(nullFilesUrls).hasSize(0),
                    () -> assertThat(emptyFilesUrls).hasSize(0)
            );
        }

        @Test
        @DisplayName("첨부파일 업로드를 성공한다")
        void success() throws Exception {
            // given
            List<MultipartFile> files = List.of(
                    createMockMultipartFile("hello1.txt", "text/plain"),
                    createMockMultipartFile("hello2.hwpx", "application/x-hwpml"),
                    createMockMultipartFile("hello3.pdf", "application/pdf"),
                    createMockMultipartFile("hello4.png", "image/png")
            );

            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl1 = new URL(String.format("https://kr.object.ncloudstorage.com/%s/attachments/1-1-1-hello1.txt", BUCKET));
            URL mockUrl2 = new URL(String.format("https://kr.object.ncloudstorage.com/%s/attachments/1-1-1-hello2.hwpx", BUCKET));
            URL mockUrl3 = new URL(String.format("https://kr.object.ncloudstorage.com/%s/attachments/1-1-1-hello3.pdf", BUCKET));
            URL mockUrl4 = new URL(String.format("https://kr.object.ncloudstorage.com/%s/attachments/1-1-1-hello4.png", BUCKET));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl1, mockUrl2, mockUrl3, mockUrl4);

            // when
            List<String> uploadUrls = uploader.uploadWeeklyAttachments(STUDY_ID, WEEK_1, MEMBER_ID, files);

            // then
            verify(amazonS3, times(files.size())).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(files.size())).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrls).containsExactlyInAnyOrder(
                    mockUrl1.toString(), mockUrl2.toString(), mockUrl3.toString(), mockUrl4.toString()
            );
        }
    }

    @Nested
    @DisplayName("Weekly 과제 제출")
    class uploadWeeklySubmit {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void fileIsNullOrEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadWeeklySubmit(STUDY_ID, WEEK_1, MEMBER_ID, nullFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadWeeklySubmit(STUDY_ID, WEEK_1, MEMBER_ID, emptyFile))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("과제물 업로드를 성공한다")
        void success() throws Exception {
            // given
            MultipartFile file = createMockMultipartFile("hello3.pdf", "application/pdf");

            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(String.format("https://kr.object.ncloudstorage.com/%s/submits/1-1-1-hello3.pdf", BUCKET));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            String uploadUrl = uploader.uploadWeeklySubmit(STUDY_ID, WEEK_1, MEMBER_ID, file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrl).isEqualTo(mockUrl.toString());
        }
    }
}
