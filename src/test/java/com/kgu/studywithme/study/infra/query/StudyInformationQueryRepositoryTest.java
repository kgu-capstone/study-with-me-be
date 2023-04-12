package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.infra.query.dto.response.CommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;
import com.kgu.studywithme.study.infra.query.dto.response.StudyApplicantInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
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

    private Member host;
    private Study study;
    private final Member[] members = new Member[5];
    private final Notice[] notices = new Notice[3];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));

        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());

        for (int i = 0; i < notices.length; i++) {
            notices[i] = Notice.writeNotice(study, "공지" + (i + 1), "내용" + (i + 1));
            noticeRepository.save(notices[i]);
        }
    }

    @Test
    @DisplayName("스터디 졸업자수를 조회한다")
    void getGraduatedParticipantCountByStudyId() {
        applyAndApproveMembers();
        assertThat(studyRepository.getGraduatedParticipantCountByStudyId(study.getId())).isEqualTo(0);

        graduate(members[0], members[1]);
        assertThat(studyRepository.getGraduatedParticipantCountByStudyId(study.getId())).isEqualTo(2);

        graduate(members[2], members[3], members[4]);
        assertThat(studyRepository.getGraduatedParticipantCountByStudyId(study.getId())).isEqualTo(5);
    }

    @Test
    @DisplayName("스터디 리뷰를 조회한다")
    void findReviewByStudyId() {
        applyAndApproveMembers();
        graduate(members[0], members[1], members[2], members[3], members[4]);

        /* 3명 리뷰 작성 */
        writeReview(members[0], members[1], members[2]);
        List<ReviewInformation> result1 = studyRepository.findReviewByStudyId(study.getId());
        assertThatReviewInformationMatch(result1, List.of(members[2], members[1], members[0]));

        /* 추가 2명 리뷰 작성 */
        writeReview(members[3], members[4]);
        List<ReviewInformation> result2 = studyRepository.findReviewByStudyId(study.getId());
        assertThatReviewInformationMatch(result2, List.of(members[4], members[3], members[2], members[1], members[0]));
    }

    @Test
    @DisplayName("스터디 공지사항 & 댓글 정보들을 조회한다")
    void findNoticeWithCommentsByStudyId() {
        // given
        List<List<Member>> commentWriters = List.of(
                List.of(members[0], members[1], members[3], members[4]),
                List.of(members[1], members[2], members[3]),
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

        /* 추가 2명 신청 & 2명 승인 */
        study.applyParticipation(members[3]);
        study.applyParticipation(members[4]);
        study.approveParticipation(members[0]);
        study.approveParticipation(members[2]);

        List<StudyApplicantInformation> result3 = studyRepository.findApplicantByStudyId(study.getId());
        assertThatApplicantsMatch(result3, List.of(members[4], members[3], members[1]));
    }

    private void applyAndApproveMembers() {
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
                    () -> assertThat(information.getNickname()).isEqualTo(member.getNicknameValue())
            );
        }
    }
}
