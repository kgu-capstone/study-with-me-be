package com.kgu.studywithme.study.service.week;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.fixture.PeriodFixture;
import com.kgu.studywithme.fixture.WeekFixture;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.controller.utils.StudyWeeklyRequestUtils;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.SubmitRepository;
import com.kgu.studywithme.study.domain.week.submit.UploadAssignment;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.upload.utils.FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Study [Service Layer] -> StudyWeeklyService 테스트")
class StudyWeeklyServiceTest extends ServiceTest {
    @Autowired
    private StudyWeeklyService studyWeeklyService;

    @MockBean
    private FileUploader fileUploader;

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SubmitRepository submitRepository;

    private Member host;
    private final Member[] members = new Member[5];
    private Study study;

    private static final WeekFixture WEEK_1 = STUDY_WEEKLY_1;
    private static final WeekFixture WEEK_2 = STUDY_WEEKLY_2;
    private static final UploadAttachment UPLOAD1 = UploadAttachment.of(
            "hello1.txt",
            "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.txt"
    );
    private static final UploadAttachment UPLOAD2 = UploadAttachment.of(
            "hello2.hwpx",
            "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.hwpx"
    );
    private static final UploadAttachment UPLOAD3 = UploadAttachment.of(
            "hello3.pdf",
            "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.pdf"
    );
    private static final UploadAttachment UPLOAD4 = UploadAttachment.of(
            "hello4.png",
            "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.png"
    );
    private static final List<UploadAttachment> uploadAttachments = List.of(UPLOAD1, UPLOAD2, UPLOAD3, UPLOAD4);
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() throws IOException {
        host = memberRepository.save(JIWON.toMember());
        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());

        study = studyRepository.save(SPRING.toOnlineStudy(host));
        beParticipation(study, members[0], members[1], members[2], members[3]);

        files = List.of(
                createMultipleMockMultipartFile("hello1.txt", "text/plain"),
                createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml"),
                createMultipleMockMultipartFile("hello3.pdf", "application/pdf"),
                createMultipleMockMultipartFile("hello4.png", "image/png")
        );
    }

    @Nested
    @DisplayName("스터디 주차 생성")
    class createWeekly {
        @Test
        @DisplayName("스터디 주차를 생성한다 [과제 O - 자동 출석 O]")
        void createWeekWithAssignmentAndAutoAttendance() {
            mockingAttachmentsUpload();

            /* given - 1주차 */
            StudyWeeklyRequest request1 = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);

            /* when - 1주차 */
            studyWeeklyService.createWeek(study.getId(), request1);

            /* then - 1주차 */
            Study findStudy1 = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks1 = findStudy1.getWeeks();
            assertThat(weeks1).hasSize(1);
            assertThatStudyWeekMatch(weeks1.get(0), 1, request1, true, true);
            assertThatAttachmentsMatch(weeks1.get(0).getAttachments(), List.of(UPLOAD1, UPLOAD2, UPLOAD3, UPLOAD4));

            List<Attendance> attendances1 = findStudy1.getAttendances();
            assertThatAttendancesMatch(
                    attendances1,
                    List.of(1, 1, 1, 1, 1),
                    List.of(host, members[0], members[1], members[2], members[3]),
                    List.of(NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE)
            );

            /* given - 2주차 */
            beParticipation(study, members[4]);
            StudyWeeklyRequest request2 = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_2, files, true);

            /* when - 2주차 */
            studyWeeklyService.createWeek(study.getId(), request2);

            /* then - 2주차 */
            Study findStudy2 = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks2 = findStudy2.getWeeks();
            assertThat(weeks2).hasSize(2);
            assertThatStudyWeekMatch(weeks2.get(0), 1, request1, true, true);
            assertThatStudyWeekMatch(weeks2.get(1), 2, request2, true, true);
            assertThatAttachmentsMatch(weeks2.get(0).getAttachments(), List.of(UPLOAD1, UPLOAD2, UPLOAD3, UPLOAD4));
            assertThatAttachmentsMatch(weeks2.get(1).getAttachments(), List.of(UPLOAD1, UPLOAD2, UPLOAD3, UPLOAD4));

            List<Attendance> attendances2 = findStudy2.getAttendances();
            assertThatAttendancesMatch(
                    attendances2,
                    List.of(
                            1, 1, 1, 1, 1,
                            2, 2, 2, 2, 2, 2
                    ),
                    List.of(
                            host, members[0], members[1], members[2], members[3],
                            host, members[0], members[1], members[2], members[3], members[4]
                    ),
                    List.of(
                            NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE,
                            NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE
                    )
            );
        }

        @Test
        @DisplayName("스터디 주차를 생성한다 [과제 O - 자동 출석 X]")
        void createWeekWithAssignmentAndManualAttendance() {
            // given
            mockingAttachmentsUpload();
            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, false);

            // when
            studyWeeklyService.createWeek(study.getId(), request);

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks = findStudy.getWeeks();
            assertThat(weeks).hasSize(1);
            assertThatStudyWeekMatch(weeks.get(0), 1, request, true, false);
            assertThatAttachmentsMatch(weeks.get(0).getAttachments(), List.of(UPLOAD1, UPLOAD2, UPLOAD3, UPLOAD4));

            List<Attendance> attendances = findStudy.getAttendances();
            assertThatAttendancesMatch(
                    attendances,
                    List.of(1, 1, 1, 1, 1),
                    List.of(host, members[0], members[1], members[2], members[3]),
                    List.of(NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE)
            );
        }

        @Test
        @DisplayName("스터디 주차를 생성한다 [과제 X]")
        void createWeek() {
            // given
            mockingAttachmentsUpload();
            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekRequest(WEEK_1, files);

            // when
            studyWeeklyService.createWeek(study.getId(), request);

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks = findStudy.getWeeks();
            assertThat(weeks).hasSize(1);
            assertThatStudyWeekMatch(weeks.get(0), 1, request, false, false);
            assertThatAttachmentsMatch(weeks.get(0).getAttachments(), List.of(UPLOAD1, UPLOAD2, UPLOAD3, UPLOAD4));

            List<Attendance> attendances = findStudy.getAttendances();
            assertThatAttendancesMatch(
                    attendances,
                    List.of(1, 1, 1, 1, 1),
                    List.of(host, members[0], members[1], members[2], members[3]),
                    List.of(NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE)
            );
        }
    }

    @Test
    @DisplayName("주차를 수정한다")
    void updateWeek() {
        // given
        study.createWeek("Week 1", "Week 1", 1, PeriodFixture.WEEK_1.toPeriod(), List.of());

        // when
        mockingAttachmentsUpload();

        StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekRequest(WEEK_2, files);
        studyWeeklyService.updateWeek(study.getId(), 1, request);

        // then
        Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), 1).orElseThrow();
        assertAll(
                () -> assertThat(findWeek.getStudy()).isEqualTo(study),
                () -> assertThat(findWeek.getCreator()).isEqualTo(host),
                () -> assertThat(findWeek.getTitle()).isEqualTo(request.title()),
                () -> assertThat(findWeek.getContent()).isEqualTo(request.content()),
                () -> assertThat(findWeek.getWeek()).isEqualTo(1),
                () -> assertThat(findWeek.getPeriod().getStartDate()).isEqualTo(request.startDate()),
                () -> assertThat(findWeek.getPeriod().getEndDate()).isEqualTo(request.endDate()),
                () -> assertThat(findWeek.isAssignmentExists()).isFalse(),
                () -> assertThat(findWeek.isAutoAttendance()).isFalse(),
                () -> assertThat(findWeek.getAttachments())
                        .map(Attachment::getUploadAttachment)
                        .map(UploadAttachment::getUploadFileName)
                        .containsExactlyInAnyOrderElementsOf(
                                uploadAttachments.stream()
                                        .map(UploadAttachment::getUploadFileName)
                                        .toList()
                        ),
                () -> assertThat(findWeek.getAttachments())
                        .map(Attachment::getUploadAttachment)
                        .map(UploadAttachment::getLink)
                        .containsExactlyInAnyOrderElementsOf(
                                uploadAttachments.stream()
                                        .map(UploadAttachment::getLink)
                                        .toList()
                        )
        );
    }

    @Nested
    @DisplayName("특정 주차 삭제")
    class deleteWeek {
        @Test
        @DisplayName("가장 최신 주차가 아님에 따라 주차를 삭제할 수 없다")
        void throwExceptionByWeekIsNotLatest() {
            // given
            study.createWeek("Week 1", "Week 1", 1, PeriodFixture.WEEK_1.toPeriod(), List.of());
            study.createWeek("Week 2", "Week 2", 2, PeriodFixture.WEEK_2.toPeriod(), List.of());

            // when - then
            assertThatThrownBy(() -> studyWeeklyService.deleteWeek(study.getId(), 1))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.WEEK_IS_NOT_LATEST.getMessage());
        }

        @Test
        @DisplayName("특정 주차를 삭제한다")
        void success() {
            // given
            study.createWeek("Week 1", "Week 1", 1, PeriodFixture.WEEK_1.toPeriod(), List.of());
            study.createWeek("Week 2", "Week 2", 2, PeriodFixture.WEEK_2.toPeriod(), List.of());

            // when
            studyWeeklyService.deleteWeek(study.getId(), 2);

            // then
            assertAll(
                    () -> assertThat(weekRepository.findByStudyIdAndWeek(study.getId(), 1)).isPresent(),
                    () -> assertThat(weekRepository.findByStudyIdAndWeek(study.getId(), 2)).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 과제 제출")
    class submitAssignment {
        @Test
        @DisplayName("과제 제출물을 업로드 하지 않으면 예외가 발생한다")
        void throwExceptionByMissingSubmission() {
            assertThatThrownBy(() -> studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "link", null, null))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MISSING_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("과제 제출물로 링크 + 파일 둘다 업로드하면 예외가 발생한다")
        void throwExceptionByDuplicateSubmission() throws IOException {
            // given
            final String submitLink = "https://notion.so";
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");

            // when - then
            assertThatThrownBy(() -> studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "link", file, submitLink))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("과제를 제출한다 [링크 + 자동 출석 O -> 출석]")
        void submitLinkWithAutoAttendance1() {
            // given
            reflectionWeekPeriod(WEEK_1, LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1));
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            studyWeeklyService.createWeek(study.getId(), request);

            assertThat(host.getScore()).isEqualTo(80);

            // when
            final String submitLink = "https://notion.so";
            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "link", null, submitLink);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isTrue(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(LINK),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isNull(),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(submitLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE),
                    () -> assertThat(host.getScore()).isEqualTo(80 + 1) // 출석 반영
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [링크 + 자동 출석 O -> 스케줄러에 의한 결석 => 지각]")
        void submitLinkWithAutoAttendance2() {
            // given
            reflectionWeekPeriod(WEEK_1, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            studyWeeklyService.createWeek(study.getId(), request);

            applyAbsenceStatusByScheduler();
            assertThat(host.getScore()).isEqualTo(80 - 5);

            // when
            final String submitLink = "https://notion.so";
            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "link", null, submitLink);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isTrue(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(LINK),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isNull(),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(submitLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(host.getScore()).isEqualTo(80 - 5 + 5 - 1) // 결석 -> 지각 반영
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [링크 + 자동 출석 O -> 지각]")
        void submitLinkWithAutoAttendance3() {
            // given
            reflectionWeekPeriod(WEEK_1, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            studyWeeklyService.createWeek(study.getId(), request);

            assertThat(host.getScore()).isEqualTo(80);

            // when
            final String submitLink = "https://notion.so";
            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "link", null, submitLink);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isTrue(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(LINK),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isNull(),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(submitLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(host.getScore()).isEqualTo(80 - 1) // 지각 반영
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [링크 + 자동 출석 X]")
        void submitLinkWithNonAutoAttendance() {
            // given
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, false);
            studyWeeklyService.createWeek(study.getId(), request);

            assertThat(host.getScore()).isEqualTo(80);

            // when
            final String submitLink = "https://notion.so";
            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "link", null, submitLink);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isFalse(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(LINK),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isNull(),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(submitLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(NON_ATTENDANCE),
                    () -> assertThat(host.getScore()).isEqualTo(80)
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [파일 + 자동 출석 O -> 출석]")
        void submitFileWithAutoAttendance1() throws IOException {
            // given
            reflectionWeekPeriod(WEEK_1, LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1));
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            studyWeeklyService.createWeek(study.getId(), request);

            assertThat(host.getScore()).isEqualTo(80);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf";
            given(fileUploader.uploadWeeklySubmit(file)).willReturn(uploadLink);

            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "file", file, null);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isTrue(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(FILE),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(uploadLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE),
                    () -> assertThat(host.getScore()).isEqualTo(80 + 1)
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [파일 + 자동 출석 O -> 스케줄러에 의한 결석 => 지각]")
        void submitFileWithAutoAttendance2() throws IOException {
            // given
            reflectionWeekPeriod(WEEK_1, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            studyWeeklyService.createWeek(study.getId(), request);

            applyAbsenceStatusByScheduler();
            assertThat(host.getScore()).isEqualTo(80 - 5);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf";
            given(fileUploader.uploadWeeklySubmit(file)).willReturn(uploadLink);

            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "file", file, null);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isTrue(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(FILE),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(uploadLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(host.getScore()).isEqualTo(80 - 5 + 5 - 1)
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [파일 + 자동 출석 O -> 지각]")
        void submitFileWithAutoAttendance3() throws IOException {
            // given
            reflectionWeekPeriod(WEEK_1, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            studyWeeklyService.createWeek(study.getId(), request);

            assertThat(host.getScore()).isEqualTo(80);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf";
            given(fileUploader.uploadWeeklySubmit(file)).willReturn(uploadLink);

            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "file", file, null);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isTrue(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(FILE),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(uploadLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                    () -> assertThat(host.getScore()).isEqualTo(80 - 1)
            );
        }

        @Test
        @DisplayName("과제를 제출한다 [파일 + 자동 출석 X]")
        void submitFileWithNonAutoAttendance() throws IOException {
            // given
            mockingAttachmentsUpload();

            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, false);
            studyWeeklyService.createWeek(study.getId(), request);

            assertThat(host.getScore()).isEqualTo(80);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/submits/uuid.pdf";
            given(fileUploader.uploadWeeklySubmit(file)).willReturn(uploadLink);

            studyWeeklyService.submitAssignment(host.getId(), study.getId(), WEEK_1.getWeek(), "file", file, null);

            // then
            Week findWeek = weekRepository.findByStudyIdAndWeek(study.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(findWeek.isAutoAttendance()).isFalse(),
                    () -> assertThat(findWeek.getSubmits()).hasSize(1)
            );

            Submit submit = findWeek.getSubmits().get(0);
            assertAll(
                    () -> assertThat(submit.getUploadAssignment().getType()).isEqualTo(FILE),
                    () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(uploadLink),
                    () -> assertThat(submit.getParticipant().getId()).isEqualTo(host.getId())
            );

            Attendance attendance = attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(attendance.getStatus()).isEqualTo(NON_ATTENDANCE),
                    () -> assertThat(host.getScore()).isEqualTo(80)
            );
        }

        private void reflectionWeekPeriod(WeekFixture fixture, LocalDateTime startDate, LocalDateTime endDate) {
            ReflectionTestUtils.setField(fixture.getPeriod(), "startDate", startDate);
            ReflectionTestUtils.setField(fixture.getPeriod(), "endDate", endDate);
        }

        private void applyAbsenceStatusByScheduler() {
            attendanceRepository.findByStudyIdAndParticipantIdAndWeek(study.getId(), host.getId(), WEEK_1.getWeek())
                    .orElseThrow()
                    .updateAttendanceStatus(ABSENCE);
            host.applyScoreByAttendanceStatus(ABSENCE);
        }
    }

    @Nested
    @DisplayName("제출한 과제 수정")
    class editSubmittedAssignment {
        @Test
        @DisplayName("과제 제출물을 업로드 하지 않으면 예외가 발생한다")
        void throwExceptionByMissingSubmission() {
            assertThatThrownBy(() -> studyWeeklyService.editSubmittedAssignment(host.getId(), WEEK_1.getWeek(), "link", null, null))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.MISSING_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("과제 제출물로 링크 + 파일 둘다 업로드하면 예외가 발생한다")
        void throwExceptionByDuplicateSubmission() throws IOException {
            // given
            final String submitLink = "https://notion.so";
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");

            // when - then
            assertThatThrownBy(() -> studyWeeklyService.editSubmittedAssignment(host.getId(), WEEK_1.getWeek(), "link", file, submitLink))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("제출한 과제가 존재하지 않는다면 과제를 수정할 수 없다")
        void throwExceptionBySubmitNotFound() {
            // given
            final String submitLink = "https://notion.so";

            // when - then
            assertThatThrownBy(() -> studyWeeklyService.editSubmittedAssignment(host.getId(), WEEK_1.getWeek(), "link", null, submitLink))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.SUBMIT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("제출한 과제를 수정한다")
        void success() {
            // given
            Week week = weekRepository.save(WEEK_1.toWeekWithAssignment(study));

            /* Case 1) File 제출 */
            // when
            final UploadAssignment previous = UploadAssignment.withFile("hello3.pdf", UPLOAD3.getLink());
            week.submitAssignment(host, previous);

            // then
            Submit previousSubmit = submitRepository.findByParticipantIdAndWeek(host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(previousSubmit.getUploadAssignment().getUploadFileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(previousSubmit.getUploadAssignment().getLink()).isEqualTo(UPLOAD3.getLink()),
                    () -> assertThat(previousSubmit.getUploadAssignment().getType()).isEqualTo(FILE),
                    () -> assertThat(previousSubmit.getWeek()).isEqualTo(week),
                    () -> assertThat(previousSubmit.getParticipant()).isEqualTo(host)
            );

            /* Case 2) 링크 제출로 수정 */
            // when
            final String uploadLink = "https://notion.so";
            studyWeeklyService.editSubmittedAssignment(host.getId(), WEEK_1.getWeek(), "link", null, uploadLink);

            // then
            Submit currentSubmit = submitRepository.findByParticipantIdAndWeek(host.getId(), WEEK_1.getWeek()).orElseThrow();
            assertAll(
                    () -> assertThat(currentSubmit.getUploadAssignment().getUploadFileName()).isNull(),
                    () -> assertThat(currentSubmit.getUploadAssignment().getLink()).isEqualTo(uploadLink),
                    () -> assertThat(currentSubmit.getUploadAssignment().getType()).isEqualTo(LINK),
                    () -> assertThat(currentSubmit.getWeek()).isEqualTo(week),
                    () -> assertThat(currentSubmit.getParticipant()).isEqualTo(host)
            );
        }
    }

    private void mockingAttachmentsUpload() {
        given(fileUploader.uploadWeeklyAttachment(files.get(0))).willReturn(UPLOAD1.getLink());
        given(fileUploader.uploadWeeklyAttachment(files.get(1))).willReturn(UPLOAD2.getLink());
        given(fileUploader.uploadWeeklyAttachment(files.get(2))).willReturn(UPLOAD3.getLink());
        given(fileUploader.uploadWeeklyAttachment(files.get(3))).willReturn(UPLOAD4.getLink());
    }

    private void beParticipation(Study study, Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    private void assertThatStudyWeekMatch(Week week, int currentWeek, StudyWeeklyRequest request,
                                          boolean isAssignmentExists, boolean isAutoAttendance) {
        assertAll(
                () -> assertThat(week.getTitle()).isEqualTo(request.title()),
                () -> assertThat(week.getContent()).isEqualTo(request.content()),
                () -> assertThat(week.getWeek()).isEqualTo(currentWeek),
                () -> assertThat(week.getPeriod().getStartDate()).isEqualTo(request.startDate()),
                () -> assertThat(week.getPeriod().getEndDate()).isEqualTo(request.endDate()),
                () -> assertThat(week.isAssignmentExists()).isEqualTo(isAssignmentExists),
                () -> assertThat(week.isAutoAttendance()).isEqualTo(isAutoAttendance),
                () -> assertThat(week.getStudy()).isEqualTo(study),
                () -> assertThat(week.getCreator()).isEqualTo(host)
        );
    }

    private static void assertThatAttachmentsMatch(List<Attachment> attachments, List<UploadAttachment> expectLinks) {
        assertAll(
                () -> assertThat(attachments).hasSize(expectLinks.size()),
                () -> assertThat(attachments)
                        .map(Attachment::getUploadAttachment)
                        .map(UploadAttachment::getUploadFileName)
                        .containsExactlyInAnyOrderElementsOf(
                                expectLinks.stream()
                                        .map(UploadAttachment::getUploadFileName)
                                        .toList()
                        ),
                () -> assertThat(attachments)
                        .map(Attachment::getUploadAttachment)
                        .map(UploadAttachment::getLink)
                        .containsExactlyInAnyOrderElementsOf(
                                expectLinks.stream()
                                        .map(UploadAttachment::getLink)
                                        .toList()
                        )
        );
    }

    private void assertThatAttendancesMatch(List<Attendance> attendances,
                                            List<Integer> expectWeeks,
                                            List<Member> expectParticipants,
                                            List<AttendanceStatus> expectStatus) {
        int totalSize = expectWeeks.size();
        assertThat(attendances).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            Attendance attendance = attendances.get(i);
            int week = expectWeeks.get(i);
            Member participant = expectParticipants.get(i);
            AttendanceStatus status = expectStatus.get(i);

            assertAll(
                    () -> assertThat(attendance.getWeek()).isEqualTo(week),
                    () -> assertThat(attendance.getParticipant()).isEqualTo(participant),
                    () -> assertThat(attendance.getStatus()).isEqualTo(status)
            );
        }
    }
}
