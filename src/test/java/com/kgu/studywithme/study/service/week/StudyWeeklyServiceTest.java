package com.kgu.studywithme.study.service.week;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.fixture.WeekFixture;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.controller.utils.StudyWeeklyRequestUtils;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.upload.utils.FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Study [Service Layer] -> StudyReviewService 테스트")
class StudyWeeklyServiceTest extends ServiceTest {
    @Autowired
    private StudyWeeklyService studyWeeklyService;

    @MockBean
    private FileUploader fileUploader;

    private Member host;
    private final Member[] members = new Member[5];
    private Study study;

    private static final WeekFixture WEEK_1 = STUDY_WEEKLY_1;
    private static final WeekFixture WEEK_2 = STUDY_WEEKLY_2;
    private static final String LINK1 = "https://kr.object.ncloudstorage.com/bucket/attachments/uuid1-hello1.txt";
    private static final String LINK2 = "https://kr.object.ncloudstorage.com/bucket/attachments/uuid2-hello2.hwpx";
    private static final String LINK3 = "https://kr.object.ncloudstorage.com/bucket/attachments/uuid3-hello3.pdf";
    private static final String LINK4 = "https://kr.object.ncloudstorage.com/bucket/attachments/uuid4-hello4.png";
    private static final List<String> uploadUrls = List.of(LINK1, LINK2, LINK3, LINK4);
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
            /* given - 1주차 */
            StudyWeeklyRequest request1 = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, true);
            mockingAttachmentsUpload(files);

            /* when - 1주차 */
            studyWeeklyService.createWeek(study.getId(), WEEK_1.getWeek(), request1);

            /* then - 1주차 */
            Study findStudy1 = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks1 = findStudy1.getWeeks();
            assertThat(weeks1).hasSize(1);
            assertThatStudyWeekMatch(weeks1.get(0), WEEK_1.getWeek(), request1, true, true);
            assertThatAttachmentsMatch(weeks1.get(0).getAttachments(), List.of(LINK1, LINK2, LINK3, LINK4));

            List<Attendance> attendances1 = findStudy1.getAttendances();
            List<Integer> expectWeeks1 = List.of(1, 1, 1, 1, 1);
            List<Member> expectParticipants1 = List.of(host, members[0], members[1], members[2], members[3]);
            List<AttendanceStatus> expectStatus1 = List.of(NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE);
            assertThatAttendancesMatch(attendances1, expectWeeks1, expectParticipants1, expectStatus1);

            /* given - 2주차 */
            beParticipation(study, members[4]);
            StudyWeeklyRequest request2 = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_2, files, true);

            /* when - 2주차 */
            studyWeeklyService.createWeek(study.getId(), WEEK_2.getWeek(), request2);

            /* then - 2주차 */
            Study findStudy2 = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks2 = findStudy2.getWeeks();
            assertThat(weeks2).hasSize(2);
            assertThatStudyWeekMatch(weeks2.get(0), WEEK_1.getWeek(), request1, true, true);
            assertThatStudyWeekMatch(weeks2.get(1), WEEK_2.getWeek(), request2, true, true);
            assertThatAttachmentsMatch(weeks2.get(0).getAttachments(), List.of(LINK1, LINK2, LINK3, LINK4));
            assertThatAttachmentsMatch(weeks2.get(1).getAttachments(), List.of(LINK1, LINK2, LINK3, LINK4));

            List<Attendance> attendances2 = findStudy2.getAttendances();
            List<Integer> expectWeeks2 = List.of(
                    1, 1, 1, 1, 1,
                    2, 2, 2, 2, 2, 2
            );
            List<Member> expectParticipants2 = List.of(
                    host, members[0], members[1], members[2], members[3],
                    host, members[0], members[1], members[2], members[3], members[4]
            );
            List<AttendanceStatus> expectStatus2 = List.of(
                    NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE,
                    NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE
            );
            assertThatAttendancesMatch(attendances2, expectWeeks2, expectParticipants2, expectStatus2);
        }

        @Test
        @DisplayName("스터디 주차를 생성한다 [과제 O - 자동 출석 X]")
        void createWeekWithAssignmentAndManualAttendance() {
            // given
            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekWithAssignmentRequest(WEEK_1, files, false);
            mockingAttachmentsUpload(files);

            // when
            studyWeeklyService.createWeek(study.getId(), WEEK_1.getWeek(), request);

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks = findStudy.getWeeks();
            assertThat(weeks).hasSize(1);
            assertThatStudyWeekMatch(weeks.get(0), WEEK_1.getWeek(), request, true, false);
            assertThatAttachmentsMatch(weeks.get(0).getAttachments(), List.of(LINK1, LINK2, LINK3, LINK4));

            List<Attendance> attendances = findStudy.getAttendances();
            List<Integer> expectWeeks = List.of(1, 1, 1, 1, 1);
            List<Member> expectParticipants = List.of(host, members[0], members[1], members[2], members[3]);
            List<AttendanceStatus> expectStatus = List.of(NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE);
            assertThatAttendancesMatch(attendances, expectWeeks, expectParticipants, expectStatus);
        }

        @Test
        @DisplayName("스터디 주차를 생성한다 [과제 X]")
        void createWeek() {
            // given
            StudyWeeklyRequest request = StudyWeeklyRequestUtils.createWeekRequest(WEEK_1, files);
            mockingAttachmentsUpload(files);

            // when
            studyWeeklyService.createWeek(study.getId(), WEEK_1.getWeek(), request);

            // then
            Study findStudy = studyRepository.findById(study.getId()).orElseThrow();

            List<Week> weeks = findStudy.getWeeks();
            assertThat(weeks).hasSize(1);
            assertThatStudyWeekMatch(weeks.get(0), WEEK_1.getWeek(), request, false, false);
            assertThatAttachmentsMatch(weeks.get(0).getAttachments(), List.of(LINK1, LINK2, LINK3, LINK4));

            List<Attendance> attendances = findStudy.getAttendances();
            List<Integer> expectWeeks = List.of(1, 1, 1, 1, 1);
            List<Member> expectParticipants = List.of(host, members[0], members[1], members[2], members[3]);
            List<AttendanceStatus> expectStatus = List.of(NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE, NON_ATTENDANCE);
            assertThatAttendancesMatch(attendances, expectWeeks, expectParticipants, expectStatus);
        }
    }

    private void mockingAttachmentsUpload(List<MultipartFile> files) {
        given(fileUploader.uploadWeeklyAttachments(files)).willReturn(uploadUrls);
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

    private static void assertThatAttachmentsMatch(List<Attachment> attachments, List<String> expectLinks) {
        assertAll(
                () -> assertThat(attachments).hasSize(expectLinks.size()),
                () -> assertThat(attachments)
                        .map(Attachment::getLink)
                        .containsExactlyInAnyOrderElementsOf(expectLinks)
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
