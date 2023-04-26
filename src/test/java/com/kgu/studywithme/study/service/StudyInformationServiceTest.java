package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.fixture.WeekFixture;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.submit.Upload;
import com.kgu.studywithme.study.infra.query.dto.response.CommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;
import com.kgu.studywithme.study.infra.query.dto.response.StudyApplicantInformation;
import com.kgu.studywithme.study.service.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyInformationService 테스트")
class StudyInformationServiceTest extends ServiceTest {
    @Autowired
    private StudyInformationService studyInformationService;

    private Member host;
    private Study study;
    private final Member[] members = new Member[4];
    private final Notice[] notices = new Notice[3];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));

        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
    }

    @Test
    @DisplayName("스터디의 기본 정보를 조회한다")
    void getInformation() {
        // when
        StudyInformation information = studyInformationService.getInformation(study.getId());

        // then
        assertAll(
                // Study
                () -> assertThat(information.id()).isEqualTo(study.getId()),
                () -> assertThat(information.name()).isEqualTo(study.getNameValue()),
                () -> assertThat(information.description()).isEqualTo(study.getDescriptionValue()),
                () -> assertThat(information.category()).isEqualTo(study.getCategory().getName()),
                () -> assertThat(information.thumbnail()).isEqualTo(study.getThumbnail().getImageName()),
                () -> assertThat(information.thumbnailBackground()).isEqualTo(study.getThumbnail().getBackground()),
                () -> assertThat(information.type()).isEqualTo(study.getType().getDescription()),
                () -> assertThat(information.location()).isNull(),
                () -> assertThat(information.recruitmentStatus()).isEqualTo(study.getRecruitmentStatus().getDescription()),
                () -> assertThat(information.currentMembers()).isEqualTo(study.getApproveParticipants().size()),
                () -> assertThat(information.maxMembers()).isEqualTo(study.getMaxMembers()),
                () -> assertThat(information.averageAge()).isEqualTo(study.getParticipantsAverageAge()),
                () -> assertThat(information.participantsAges()).containsExactlyInAnyOrderElementsOf(study.getParticipantsAges()),
                () -> assertThat(information.hashtags()).containsExactlyInAnyOrderElementsOf(study.getHashtags()),
                // Host
                () -> assertThat(information.host().id()).isEqualTo(host.getId()),
                () -> assertThat(information.host().nickname()).isEqualTo(host.getNicknameValue())
        );
    }
    
    @Test
    @DisplayName("스터디 졸업자들의 리뷰를 조회한다")
    void getReviews() {
        // given
        applyAndApproveMembers(members[0], members[1], members[2], members[3]);
        graduateAllParticipant();
        
        // when
        ReviewAssembler result = studyInformationService.getReviews(study.getId());

        // then
        assertAll(
                () -> assertThat(result.graduateCount()).isEqualTo(members.length),
                () -> assertThat(result.reviews()).hasSize(members.length),
                () -> {
                    List<String> reviewers = result.reviews()
                            .stream()
                            .map(ReviewInformation::getReviewer)
                            .map(StudyMember::nickname)
                            .toList();

                    assertThat(reviewers).containsExactlyInAnyOrderElementsOf(
                            Arrays.stream(members)
                                    .map(Member::getNicknameValue)
                                    .toList()
                    );
                }
        );
    }

    @Test
    @DisplayName("스터디 공지사항 & 댓글 정보들을 조회한다")
    void getNotices() {
        // given
        initNotices();
        List<List<Member>> commentWriters = List.of(
                List.of(members[0], members[1], members[3]),
                List.of(members[1], members[2]),
                List.of()
        );
        writeComment(notices[0], commentWriters.get(0));
        writeComment(notices[1], commentWriters.get(1));
        writeComment(notices[2], commentWriters.get(2));

        // when
        NoticeAssembler assembler = studyInformationService.getNotices(study.getId());

        List<NoticeInformation> result = assembler.result();
        assertThat(result).hasSize(3);
        assertThatNoticeInformationMatch(result.get(0), notices[2], commentWriters.get(2));
        assertThatNoticeInformationMatch(result.get(1), notices[1], commentWriters.get(1));
        assertThatNoticeInformationMatch(result.get(2), notices[0], commentWriters.get(0));
    }

    @Test
    @DisplayName("스터디 신청자 정보를 조회한다")
    void getApplicants() {
        StudyApplicant result1 = studyInformationService.getApplicants(study.getId());
        assertThatApplicantsMatch(result1.applicants(), List.of());

        /* 신청자 3명 */
        study.applyParticipation(members[0]);
        study.applyParticipation(members[1]);
        study.applyParticipation(members[2]);

        StudyApplicant result2 = studyInformationService.getApplicants(study.getId());
        assertThatApplicantsMatch(result2.applicants(), List.of(members[2], members[1], members[0]));

        /* 추가 1명 신청 & 2명 승인 */
        study.applyParticipation(members[3]);
        study.approveParticipation(members[0]);
        study.approveParticipation(members[2]);

        StudyApplicant result3 = studyInformationService.getApplicants(study.getId());
        assertThatApplicantsMatch(result3.applicants(), List.of(members[3], members[1]));
    }

    @Test
    @DisplayName("스터디 주차별 출석 정보를 조회한다")
    void getAttendances() {
        applyAndApproveMembers(members[0], members[1], members[2]);

        /* 1주차 출석 */
        applyAttendance(
                1,
                Map.of(
                        host, ATTENDANCE,
                        members[0], ATTENDANCE,
                        members[1], LATE,
                        members[2], ABSENCE
                )
        );
        AttendanceAssmbler result1 = studyInformationService.getAttendances(study.getId());
        List<Integer> expectWeek1 = List.of(1);
        List<Map<Long, AttendanceStatus>> expectSummary1 = List.of(
                Map.of(
                        host.getId(), ATTENDANCE,
                        members[0].getId(), ATTENDANCE,
                        members[1].getId(), LATE,
                        members[2].getId(), ABSENCE
                )
        );
        assertThatAttendancesMatch(result1.summaries(), expectWeek1, expectSummary1);

        /* 1주차 + 2주차 출석 */
        applyAndApproveMembers(members[3]);
        applyAttendance(
                2,
                Map.of(
                        host, ATTENDANCE,
                        members[0], LATE,
                        members[1], ATTENDANCE,
                        members[2], ATTENDANCE,
                        members[3], ATTENDANCE
                )
        );
        AttendanceAssmbler result2 = studyInformationService.getAttendances(study.getId());
        List<Integer> expectWeek2 = List.of(2, 1);
        List<Map<Long, AttendanceStatus>> expectSummary2 = List.of(
                Map.of(
                        host.getId(), ATTENDANCE,
                        members[0].getId(), LATE,
                        members[1].getId(), ATTENDANCE,
                        members[2].getId(), ATTENDANCE,
                        members[3].getId(), ATTENDANCE
                ),
                Map.of(
                        host.getId(), ATTENDANCE,
                        members[0].getId(), ATTENDANCE,
                        members[1].getId(), LATE,
                        members[2].getId(), ABSENCE
                )
        );
        assertThatAttendancesMatch(result2.summaries(), expectWeek2, expectSummary2);
    }

    @Test
    @DisplayName("스터디 각 주차를 조회한다")
    void getWeeks() {
        // given
        applyAndApproveMembers(members[0], members[1], members[2], members[3]);

        Week week1 = addWeek(STUDY_WEEKLY_1, true);
        Week week2 = addWeek(STUDY_WEEKLY_2, true);
        Week week3 = addWeek(STUDY_WEEKLY_3, true);
        Week week4 = addWeek(STUDY_WEEKLY_4, true);
        Week week5 = addWeek(STUDY_WEEKLY_5, false);
        Week week6 = addWeek(STUDY_WEEKLY_6, false);

        submitLinkAssignment(week1, "https://notion.so", host, members[0], members[1], members[2], members[3]);
        submitLinkAssignment(week2, "https://notion.so", host);
        submitLinkAssignment(week3, "https://notion.so", host, members[0], members[1]);
        submitLinkAssignment(week4, "https://notion.so", members[0], members[2], members[3]);

        // when
        WeeklyAssembler weeklyAssembler = studyInformationService.getWeeks(study.getId());
        List<WeeklySummary> weeks = weeklyAssembler.weeks();

        // then
        assertAll(
                () -> assertThat(weeks).hasSize(6),
                () -> assertThat(weeks)
                        .map(WeeklySummary::id)
                        .containsExactly(week6.getId(), week5.getId(), week4.getId(), week3.getId(), week2.getId(), week1.getId()),
                () -> assertThat(weeks)
                        .map(WeeklySummary::title)
                        .containsExactly(week6.getTitle(), week5.getTitle(), week4.getTitle(), week3.getTitle(), week2.getTitle(), week1.getTitle()),
                () -> assertThat(weeks)
                        .map(WeeklySummary::content)
                        .containsExactly(week6.getContent(), week5.getContent(), week4.getContent(), week3.getContent(), week2.getContent(), week1.getContent()),
                () -> assertThat(weeks)
                        .map(WeeklySummary::week)
                        .containsExactly(week6.getWeek(), week5.getWeek(), week4.getWeek(), week3.getWeek(), week2.getWeek(), week1.getWeek()),
                () -> assertThat(weeks)
                        .map(WeeklySummary::period)
                        .containsExactly(week6.getPeriod(), week5.getPeriod(), week4.getPeriod(), week3.getPeriod(), week2.getPeriod(), week1.getPeriod()),
                () -> assertThat(weeks)
                        .map(WeeklySummary::creator)
                        .map(StudyMember::id)
                        .containsExactly(host.getId(), host.getId(), host.getId(), host.getId(), host.getId(), host.getId()),
                () -> assertThat(weeks)
                        .map(WeeklySummary::creator)
                        .map(StudyMember::nickname)
                        .containsExactly(
                                host.getNicknameValue(),
                                host.getNicknameValue(),
                                host.getNicknameValue(),
                                host.getNicknameValue(),
                                host.getNicknameValue(),
                                host.getNicknameValue()
                        ),
                () -> assertThat(weeks)
                        .map(WeeklySummary::assignmentExists)
                        .containsExactly(
                                STUDY_WEEKLY_6.isAssignmentExists(),
                                STUDY_WEEKLY_5.isAssignmentExists(),
                                STUDY_WEEKLY_4.isAssignmentExists(),
                                STUDY_WEEKLY_3.isAssignmentExists(),
                                STUDY_WEEKLY_2.isAssignmentExists(),
                                STUDY_WEEKLY_1.isAssignmentExists()
                        ),
                () -> assertThat(weeks)
                        .map(WeeklySummary::autoAttendance)
                        .containsExactly(
                                STUDY_WEEKLY_6.isAutoAttendance(),
                                STUDY_WEEKLY_5.isAutoAttendance(),
                                STUDY_WEEKLY_4.isAutoAttendance(),
                                STUDY_WEEKLY_3.isAutoAttendance(),
                                STUDY_WEEKLY_2.isAutoAttendance(),
                                STUDY_WEEKLY_1.isAutoAttendance()
                        ),
                () -> assertThat(weeks)
                        .map(WeeklySummary::attachments)
                        .containsExactly(
                                STUDY_WEEKLY_6.getAttachments(),
                                STUDY_WEEKLY_5.getAttachments(),
                                STUDY_WEEKLY_4.getAttachments(),
                                STUDY_WEEKLY_3.getAttachments(),
                                STUDY_WEEKLY_2.getAttachments(),
                                STUDY_WEEKLY_1.getAttachments()
                        ),
                () -> {
                    List<List<WeeklySubmitSummary>> submits = weeks.stream()
                            .map(WeeklySummary::submits)
                            .toList();

                    List<List<Long>> participantIds = submits.stream()
                            .map(submit -> submit.stream()
                                    .map(WeeklySubmitSummary::participant)
                                    .toList())
                            .map(participant -> participant.stream()
                                    .map(StudyMember::id)
                                    .toList())
                            .toList();
                    assertThat(participantIds).containsExactlyInAnyOrder(
                            List.of(host.getId(), members[0].getId(), members[1].getId(), members[2].getId(), members[3].getId()),
                            List.of(host.getId()),
                            List.of(host.getId(), members[0].getId(), members[1].getId()),
                            List.of(members[0].getId(), members[2].getId(), members[3].getId()),
                            List.of(),
                            List.of()
                    );

                    List<List<String>> participantNicknames = submits.stream()
                            .map(submit -> submit.stream()
                                    .map(WeeklySubmitSummary::participant)
                                    .toList())
                            .map(participant -> participant.stream()
                                    .map(StudyMember::nickname)
                                    .toList())
                            .toList();
                    assertThat(participantNicknames).containsExactlyInAnyOrder(
                            List.of(
                                    host.getNicknameValue(),
                                    members[0].getNicknameValue(),
                                    members[1].getNicknameValue(),
                                    members[2].getNicknameValue(),
                                    members[3].getNicknameValue()
                            ),
                            List.of(
                                    host.getNicknameValue()
                            ),
                            List.of(
                                    host.getNicknameValue(),
                                    members[0].getNicknameValue(),
                                    members[1].getNicknameValue()
                            ),
                            List.of(
                                    members[0].getNicknameValue(),
                                    members[2].getNicknameValue(),
                                    members[3].getNicknameValue()
                            ),
                            List.of(),
                            List.of()
                    );

                    List<List<String>> submitLinks = submits.stream()
                            .map(submit -> submit.stream()
                                    .map(WeeklySubmitSummary::submitLink)
                                    .toList())
                            .toList();
                    assertThat(submitLinks).containsExactlyInAnyOrder(
                            List.of(
                                    "https://notion.so" + host.getId(),
                                    "https://notion.so" + members[0].getId(),
                                    "https://notion.so" + members[1].getId(),
                                    "https://notion.so" + members[2].getId(),
                                    "https://notion.so" + members[3].getId()
                            ),
                            List.of(
                                    "https://notion.so" + host.getId()
                            ),
                            List.of(
                                    "https://notion.so" + host.getId(),
                                    "https://notion.so" + members[0].getId(),
                                    "https://notion.so" + members[1].getId()
                            ),
                            List.of(
                                    "https://notion.so" + members[0].getId(),
                                    "https://notion.so" + members[2].getId(),
                                    "https://notion.so" + members[3].getId()
                            ),
                            List.of(),
                            List.of()
                    );
                }
        );
    }

    private void applyAndApproveMembers(Member... members) {
        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    private void graduateAllParticipant() {
        for (Member member : members) {
            study.graduateParticipant(member);
            study.writeReview(member, "좋은 스터디");
        }
    }

    private void initNotices() {
        for (int i = 0; i < notices.length; i++) {
            notices[i] = Notice.writeNotice(study, "공지" + (i + 1), "내용" + (i + 1));
            noticeRepository.save(notices[i]);
        }
    }

    private void writeComment(Notice notice, List<Member> members) {
        for (Member member : members) {
            commentRepository.save(Comment.writeComment(notice, member, "댓글"));
        }
    }

    private void applyAttendance(int week, Map<Member, AttendanceStatus> data) {
        for (Member member : data.keySet()) {
            study.recordAttendance(member, week, data.get(member));
        }
    }

    private Week addWeek(WeekFixture fixture, boolean isAssignmentExists) {
        if (isAssignmentExists) {
            return weekRepository.save(fixture.toWeekWithAssignment(study));
        } else {
            return weekRepository.save(fixture.toWeek(study));
        }
    }

    private void submitLinkAssignment(Week week, String link, Member... participants) {
        for (Member participant : participants) {
            week.submitAssignment(participant, Upload.withLink(link + participant.getId()));
        }
    }

    private void assertThatNoticeInformationMatch(NoticeInformation information, Notice notice, List<Member> members) {
        assertAll(
                () -> assertThat(information.getId()).isEqualTo(notice.getId()),
                () -> assertThat(information.getTitle()).isEqualTo(notice.getTitle()),
                () -> assertThat(information.getContent()).isEqualTo(notice.getContent()),
                () -> assertThat(information.getWriter().id()).isEqualTo(host.getId()),
                () -> assertThat(information.getWriter().nickname()).isEqualTo(host.getNicknameValue())
        );

        final int totalCommentsSize = members.size();
        List<CommentInformation> comments = information.getComments();
        assertThat(comments).hasSize(totalCommentsSize);

        for (int i = 0; i < totalCommentsSize; i++) {
            CommentInformation comment = comments.get(i);
            Member member = members.get(i);

            assertAll(
                    () -> assertThat(comment.getWriter().id()).isEqualTo(member.getId()),
                    () -> assertThat(comment.getWriter().nickname()).isEqualTo(member.getNicknameValue())
            );
        }
    }

    private void assertThatApplicantsMatch(List<StudyApplicantInformation> result, List<Member> members) {
        final int totalSize = members.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            StudyApplicantInformation information = result.get(i);
            Member member = members.get(i);

            assertAll(
                    () -> assertThat(information.getId()).isEqualTo(member.getId()),
                    () -> assertThat(information.getNickname()).isEqualTo(member.getNicknameValue())
            );
        }
    }

    private void assertThatAttendancesMatch(Map<Integer, List<AttendanceSummary>> result,
                                            List<Integer> weeks,
                                            List<Map<Long, AttendanceStatus>> expectSummary) {
        int totalSize = weeks.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < weeks.size(); i++) {
            int week = weeks.get(i);
            List<AttendanceSummary> attendanceSummaries = result.get(week);
            Map<Long, AttendanceStatus> expectAttendanceSummaries = expectSummary.get(i);

            for (AttendanceSummary summary : attendanceSummaries) {
                StudyMember participant = summary.participant();
                String status = summary.status();

                assertAll(
                        () -> assertThat(expectAttendanceSummaries.containsKey(participant.id())).isTrue(),
                        () -> assertThat(expectAttendanceSummaries.get(participant.id()).getDescription()).isEqualTo(status)
                );
            }
        }
    }
}
