package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.fixture.WeekFixture;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.UploadAssignment;
import com.kgu.studywithme.study.infra.query.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> StudyInformationQueryRepository 테스트")
class StudyInformationQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private WeekRepository weekRepository;

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

        for (int i = 0; i < notices.length; i++) {
            notices[i] = Notice.writeNotice(study, "공지" + (i + 1), "내용" + (i + 1));
            noticeRepository.save(notices[i]);
        }
    }

    @Test
    @DisplayName("스터디 졸업자수를 조회한다")
    void getGraduatedParticipantCountByStudyId() {
        applyAndApproveMembers(members[0], members[1], members[2], members[3]);
        assertThat(studyRepository.getGraduatedParticipantCountByStudyId(study.getId())).isEqualTo(0);

        graduate(members[0], members[1], members[2]);
        assertThat(studyRepository.getGraduatedParticipantCountByStudyId(study.getId())).isEqualTo(3);

        graduate(members[3]);
        assertThat(studyRepository.getGraduatedParticipantCountByStudyId(study.getId())).isEqualTo(4);
    }

    @Test
    @DisplayName("스터디 리뷰를 조회한다")
    void findReviewByStudyId() {
        applyAndApproveMembers(members[0], members[1], members[2], members[3]);
        graduate(members[0], members[1], members[2], members[3]);

        /* 3명 리뷰 작성 */
        writeReview(members[0], members[1], members[2]);
        List<ReviewInformation> result1 = studyRepository.findReviewByStudyId(study.getId());
        assertThatReviewInformationMatch(result1, List.of(members[2], members[1], members[0]));

        /* 추가 1명 리뷰 작성 */
        writeReview(members[3]);
        List<ReviewInformation> result2 = studyRepository.findReviewByStudyId(study.getId());
        assertThatReviewInformationMatch(result2, List.of(members[3], members[2], members[1], members[0]));
    }

    @Test
    @DisplayName("스터디 공지사항 & 댓글 정보들을 조회한다")
    void findNoticeWithCommentsByStudyId() {
        // given
        List<List<Member>> commentWriters = List.of(
                List.of(members[0], members[1], members[3]),
                List.of(members[1], members[2]),
                List.of()
        );
        writeComment(notices[0], commentWriters.get(0));
        writeComment(notices[1], commentWriters.get(1));
        writeComment(notices[2], commentWriters.get(2));

        // when
        List<NoticeInformation> result = studyRepository.findNoticeWithCommentsByStudyId(study.getId());
        assertThat(result).hasSize(3);
        assertThatNoticeInformationMatch(result.get(0), notices[2], commentWriters.get(2));
        assertThatNoticeInformationMatch(result.get(1), notices[1], commentWriters.get(1));
        assertThatNoticeInformationMatch(result.get(2), notices[0], commentWriters.get(0));
    }

    @Test
    @DisplayName("스터디 신청자 정보를 조회한다")
    void findApplicantByStudyId() {
        List<StudyApplicantInformation> result1 = studyRepository.findApplicantByStudyId(study.getId());
        assertThatApplicantsMatch(result1, List.of());

        /* 신청자 3명 */
        study.applyParticipation(members[0]);
        study.applyParticipation(members[1]);
        study.applyParticipation(members[2]);

        List<StudyApplicantInformation> result2 = studyRepository.findApplicantByStudyId(study.getId());
        assertThatApplicantsMatch(result2, List.of(members[2], members[1], members[0]));

        /* 추가 1명 신청 & 2명 승인 */
        study.applyParticipation(members[3]);
        study.approveParticipation(members[0]);
        study.approveParticipation(members[2]);

        List<StudyApplicantInformation> result3 = studyRepository.findApplicantByStudyId(study.getId());
        assertThatApplicantsMatch(result3, List.of(members[3], members[1]));
    }

    @Test
    @DisplayName("스터디 주차별 출석 정보를 조회한다")
    void findAttendanceByStudyId() {
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
        List<AttendanceInformation> result1 = studyRepository.findAttendanceByStudyId(study.getId());
        assertThatAttendancesMatch(
                result1,
                List.of(1, 1, 1, 1),
                List.of(host, members[0], members[1], members[2]),
                List.of(ATTENDANCE, ATTENDANCE, LATE, ABSENCE)
        );

        /* 1주차 + 2주차 출석 */
        applyAndApproveMembers(members[3]);
        graduate(members[0]);
        applyAttendance(
                2,
                Map.of(
                        host, LATE,
                        members[1], ATTENDANCE,
                        members[2], ATTENDANCE,
                        members[3], ATTENDANCE
                )
        );
        List<AttendanceInformation> result2 = studyRepository.findAttendanceByStudyId(study.getId());
        assertThatAttendancesMatch(
                result2,
                List.of(
                        1, 2,
                        1, 2,
                        1, 2,
                        2
                ),
                List.of(
                        host, host,
                        members[1], members[1],
                        members[2], members[2],
                        members[3]
                ),
                List.of(
                        ATTENDANCE, LATE,
                        LATE, ATTENDANCE,
                        ABSENCE, ATTENDANCE,
                        ATTENDANCE
                )
        );

        /* 1주차 + 2주차 + 3주차 출석 */
        graduate(members[2]);
        applyAttendance(
                3,
                Map.of(
                        host, ATTENDANCE,
                        members[1], ATTENDANCE,
                        members[3], ATTENDANCE
                )
        );
        List<AttendanceInformation> result3 = studyRepository.findAttendanceByStudyId(study.getId());
        assertThatAttendancesMatch(
                result3,
                List.of(
                        1, 2, 3,
                        1, 2, 3,
                        2, 3
                ),
                List.of(
                        host, host, host,
                        members[1], members[1], members[1],
                        members[3], members[3]
                ),
                List.of(
                        ATTENDANCE, LATE, ATTENDANCE,
                        LATE, ATTENDANCE, ATTENDANCE,
                        ATTENDANCE, ATTENDANCE
                )
        );
    }

    @Test
    @DisplayName("스터디 각 주차를 조회한다")
    void findWeeklyByStudyId() {
        // given
        applyAndApproveMembers(members[0], members[1], members[2], members[3]);

        Week week1 = addWeek(STUDY_WEEKLY_1, true);
        Week week2 = addWeek(STUDY_WEEKLY_2, true);
        Week week3 = addWeek(STUDY_WEEKLY_3, true);
        Week week4 = addWeek(STUDY_WEEKLY_4, true);
        addWeek(STUDY_WEEKLY_5, false);
        addWeek(STUDY_WEEKLY_6, false);

        submitLinkAssignment(week1, "https://notion.so", host, members[0], members[1], members[2], members[3]);
        submitLinkAssignment(week2, "https://notion.so", host);
        submitLinkAssignment(week3, "https://notion.so", host, members[0], members[1]);
        submitLinkAssignment(week4, "https://notion.so", members[0], members[2], members[3]);

        // when
        List<Week> weeks = studyRepository.findWeeklyByStudyId(study.getId());

        // then
        assertAll(
                () -> assertThat(weeks).hasSize(6),
                () -> assertThat(weeks)
                        .map(Week::getWeek)
                        .containsExactly(
                                STUDY_WEEKLY_6.getWeek(),
                                STUDY_WEEKLY_5.getWeek(),
                                STUDY_WEEKLY_4.getWeek(),
                                STUDY_WEEKLY_3.getWeek(),
                                STUDY_WEEKLY_2.getWeek(),
                                STUDY_WEEKLY_1.getWeek()
                        ),
                () -> {
                    List<List<UploadAttachment>> attachmentLinks = weeks.stream()
                            .map(Week::getAttachments)
                            .map(attachments -> attachments.stream()
                                    .map(Attachment::getUploadAttachment)
                                    .toList())
                            .toList();
                    assertThat(attachmentLinks).containsExactlyInAnyOrder(
                            STUDY_WEEKLY_1.getAttachments(),
                            STUDY_WEEKLY_2.getAttachments(),
                            STUDY_WEEKLY_3.getAttachments(),
                            STUDY_WEEKLY_4.getAttachments(),
                            STUDY_WEEKLY_5.getAttachments(),
                            STUDY_WEEKLY_6.getAttachments()
                    );
                },
                () -> {
                    List<List<Submit>> submits = weeks.stream()
                            .map(Week::getSubmits)
                            .toList();

                    List<List<Member>> participants = submits.stream()
                            .map(submit -> submit.stream()
                                    .map(Submit::getParticipant)
                                    .toList())
                            .toList();
                    assertThat(participants).containsExactlyInAnyOrder(
                            List.of(host, members[0], members[1], members[2], members[3]),
                            List.of(host),
                            List.of(host, members[0], members[1]),
                            List.of(members[0], members[2], members[3]),
                            List.of(),
                            List.of()
                    );

                    List<List<String>> links = submits.stream()
                            .map(submit -> submit.stream()
                                    .map(Submit::getUploadAssignment)
                                    .map(UploadAssignment::getLink)
                                    .toList())
                            .toList();
                    assertThat(links).containsExactlyInAnyOrder(
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

    private void graduate(Member... members) {
        for (Member member : members) {
            study.graduateParticipant(member);
        }
    }

    private void writeReview(Member... members) {
        for (Member member : members) {
            study.writeReview(member, "리뷰");
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
            week.submitAssignment(participant, UploadAssignment.withLink(link + participant.getId()));
        }
    }

    private void assertThatReviewInformationMatch(List<ReviewInformation> result, List<Member> members) {
        final int totalSize = members.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            ReviewInformation review = result.get(i);
            Member member = members.get(i);

            assertAll(
                    () -> assertThat(review.getReviewer().id()).isEqualTo(member.getId()),
                    () -> assertThat(review.getReviewer().nickname()).isEqualTo(member.getNicknameValue())
            );
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
                    () -> assertThat(information.getNickname()).isEqualTo(member.getNicknameValue()),
                    () -> assertThat(information.getScore()).isEqualTo(member.getScore())
            );
        }
    }

    private void assertThatAttendancesMatch(List<AttendanceInformation> result,
                                            List<Integer> weeks,
                                            List<Member> members,
                                            List<AttendanceStatus> attendanceStatuses) {
        int totalSize = attendanceStatuses.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            AttendanceInformation information = result.get(i);
            int week = weeks.get(i);
            Member member = members.get(i);
            AttendanceStatus attendanceStatus = attendanceStatuses.get(i);

            assertAll(
                    () -> assertThat(information.getParticipant().id()).isEqualTo(member.getId()),
                    () -> assertThat(information.getParticipant().nickname()).isEqualTo(member.getNicknameValue()),
                    () -> assertThat(information.getWeek()).isEqualTo(week),
                    () -> assertThat(information.getAttendanceStatus()).isEqualTo(attendanceStatus.getDescription())
            );
        }
    }
}
